package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.hardware.*;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.AllocationException;

/**
 * Handle motor outputs and feedback for an individual swerve wheel
 * 
 * @author Team 4818 WFRobotics
 */
public class SwerveWheel {
    /** 'Name' of the module */
    public String name;
    /** Index/number of the module*/
    public int number;

    /** Wheel location from center of robot */
    private Vector position;
    /** Desired wheel vector, from input */
    private Vector desired;
    /** Actual wheel vector, from sensors */
    private Vector actual;

    /** Gear Shifter servo */
    private Servo gearShifter;
    /** Angle for high gear */
    public int[] gearShifterAngle;
    /** If it's in high gear or not */
    private boolean gearHigh;

    /** If the brake should be applied or not */
    private boolean brake;

    /** Drive motor object */
    private CANTalon driveMotor;
    private double driveLastSpeed;
    private double driveLastChangeTime;
    
    /** Angle motor object */
    private CANTalon angleMotor;
    /** Angle sensor */
    private MagnetoPot anglePot;
    /** Max speed the rotation can spin, relative to motor maximum */
    private double angleMaxSpeed = -.7;
    /** Special angle PID controller */
    private SwerveAngleController anglePID;

    /** Invert the angle motor and sensor to swap left/right */
    private boolean angleInverted = true;
    /** Auto calibration sensor for having a known angle */
    private DigitalInput angleCalSensor;
    /** Enable/disable the auto calibration */
    private boolean angleCalEnable;
    /** Angle that the calibration sensor is mounted compared to 0 */
    public double angelCalSensorOffset;
    /** Threshold for triggering the calibration sensor */
    private final double angleCalSensorThreshold = 10;

    // private static final double DriveP = 0.3;
    // private static final double DriveD= 0.5; // seconds needed to equal a P
    // // term contribution
    // private static final double DriveI = 0;// 2 / DriveTI;
    // private static double DriveRampRate = 12; // volt/sec change
    // private static double DriveIzone = 123;

    /** Minimum speed, used for dead band */ 
    private static final double MINIMUM_SPEED = 0.1;

    /**
     * Set up a swerve wheel using pin and address assignments,
     * using an analog input for angle
     * @param Number
     * @param Position
     * @param PotPin
     * @param DriveAddress
     * @param AngleAddress
     * @param ShiftPin
     * @param ShiftVals
     * @param AngleCalibrationPin
     */
    public SwerveWheel(int Number, double[] Position, int PotPin,
            int DriveAddress, int AngleAddress, int ShiftPin, int[] ShiftVals,
            int AngleCalibrationPin)
    {
        this(Number, new Vector(Position), new MagnetoPotAnalog(PotPin, 360),
                new CANTalon(DriveAddress), new CANTalon(AngleAddress),
                new Servo(ShiftPin), ShiftVals,
                new DigitalInput(AngleCalibrationPin));
    }

    /**
     * Set up a swerve wheel using pin and address assignments, 
     * using the SRX input for angle
     * @param Number
     * @param Position
     * @param DriveAddress
     * @param AngleAddress
     * @param ShiftPin
     * @param ShiftVals
     * @param AngleCalibrationPin
     */
    public SwerveWheel(int Number, double[] Position, 
            int DriveAddress, int AngleAddress, int ShiftPin, int[] ShiftVals,
            int AngleCalibrationPin)
    {
        this(Number, new Vector(Position), 
                new CANTalon(DriveAddress), new CANTalon(AngleAddress),
                new Servo(ShiftPin), ShiftVals,
                new DigitalInput(AngleCalibrationPin));
    }

    /**
     * Set up a swerve wheel using controllers/objects,
     * using a generic sensor for angle
     * @param Number
     * @param Position
     * @param Pot
     * @param DriveMotor
     * @param AngleMotor
     * @param Shifter
     * @param ShiftVals
     * @param Calibration
     */
    public SwerveWheel(int Number, Vector Position, MagnetoPot Pot,
            CANTalon DriveMotor, CANTalon AngleMotor, Servo Shifter,
            int[] ShiftVals, DigitalInput Calibration)
    {
        name = "Wheel" + Number;
        this.number = Number;

        position = Position;
        actual = new Vector(0, 0);
        desired = new Vector(0, 0);
        driveMotor = DriveMotor;

        // MotorDrive.setPID(DriveP, DriveI, DriveD, 0, izone,
        // closeLoopRampRate, 0);
        // MotorDrive.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        // MotorDrive.changeControlMode(ControlMode.Disabled);

        angleMotor = AngleMotor;

        gearHigh = true;
        this.gearShifter = Shifter;
        gearShifterAngle = ShiftVals;

        anglePot = Pot;
        anglePID = new SwerveAngleController(name + ".ctl");

        angleCalSensor = Calibration;
    }
    
    /**
     * Set up a swerve wheel using controllers/objects,
     * using an SRX input for angle
     * @param Number
     * @param Position
     * @param DriveMotor
     * @param AngleMotor
     * @param Shifter
     * @param ShiftVals
     * @param Calibration
     */
    public SwerveWheel(int Number, Vector Position, 
            CANTalon DriveMotor, CANTalon AngleMotor, Servo Shifter,
            int[] ShiftVals, DigitalInput Calibration)
    {
        name = "Wheel" + Number;
        this.number = Number;

        position = Position;
        actual = new Vector(0, 0);
        desired = new Vector(0, 0);
        driveMotor = DriveMotor;

        driveMotor.setVoltageRampRate(20);
        driveLastChangeTime = Timer.getFPGATimestamp();
        //driveMotor.setCurrentLimit(5);
        
        if(SwerveConstants.DRIVE_MOTOR_SPEED_SENSOR_ENABLE)
        {
            driveMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            driveMotor.changeControlMode(TalonControlMode.Speed);
            driveMotor.setPID(SwerveConstants.DRIVE_PID_P,
                              SwerveConstants.DRIVE_PID_I,
                              SwerveConstants.DRIVE_PID_D);
        }

        angleMotor = AngleMotor;

        gearHigh = true;
        this.gearShifter = Shifter;
        gearShifterAngle = ShiftVals;

        anglePot = new MagnetoPotSRX(angleMotor, 360);
        anglePID = new SwerveAngleController(name + ".ctl");

        angleCalSensor = Calibration;
    }

    public void free()
    {
        //try { gearShifter.free(); } catch (AllocationException e) {}
        try { anglePot.free(); } catch (AllocationException e) {}
        try { angleCalSensor.free(); } catch (AllocationException e) {}
    }
    
    /**
     * Set the desired wheel vector, auto updates the PID controllers
     * 
     * @param newDesired
     * @param gearHigh
     * @return Actual vector reading of wheel
     */
    public Vector setDesired(Vector newDesired, boolean newHighGear,
            boolean newBrake)
    {
        // store off the new values
        desired = newDesired;
        gearHigh = newHighGear;
        brake = newBrake;

        return updateTask();
    }

    private double getAnglePotAdjusted()
    {
        double invert = angleInverted ? -1 : 1;
        return Utilities.wrapToRange(invert * anglePot.get(),-180,180);
    }
    
    /**
     * Get the desired vector (velocity and rotation) of this wheel instance
     * 
     * @return Desired vector
     */
    public Vector getDesired()
    {
        return desired;
    }

    /**
     * Get the actual vector reading of the wheel
     * 
     * @return Actual vector reading of wheel
     */
    public Vector getActual()
    {
        // WheelActual.setMagAngle(DriveEncoder.getRate(), getAnglePotValue());
        if(SwerveConstants.DRIVE_MOTOR_SPEED_SENSOR_ENABLE)
        {
            actual.setMagAngle(driveMotor.getSpeed()/SwerveConstants.DriveMaxSpeed, getAnglePotAdjusted());
        }
        else
        {
            actual.setMagAngle(desired.getMag(), getAnglePotAdjusted());
        }
        return actual;
    }

    /**
     * Get the wheel's position relative to robot center
     * 
     * @return Wheel position
     */
    public Vector getPosition()
    {
        return position;
    }

    /**
     * Get whether the wheel is in high gear or low gear
     * 
     * @return Whether the wheel is in high gear
     */
    public boolean getHighGear()
    {
        return gearHigh;
    }

    private void updateShifter()
    {
        if (gearHigh)
        {
            gearShifter.setAngle(gearShifterAngle[0]);

        }
        else
        {
            gearShifter.setAngle(gearShifterAngle[1]);
        }
    }

    /**
     * auto calibrate if enable and if the calibration sensor is triggered
     */
    private void autoCalibration()
    {

        if (angleCalEnable && angleCalSensor.get())
        {
            // the Calibration Sensor is triggered, so we should be facing
            // forward
            if (Math.abs(getAnglePotAdjusted()) > angleCalSensorThreshold)
            {
                // we're more than angleCalSensorThreshold away, yet the sensor 
                // is triggered, we need to then update the angle
                
                //TODO 
//                this.AngleOrientation = Utilities.wrapToRange(270 - angle, 0,
//                360);
//                
//                saveAngleOrientation(AngleOrientation);
//                
//                AdjustedAngle = Utilities.wrapToRange(angle + 270 -
//                AngleOrientation, 0, 360);
//                Preferences.getInstance().putDouble("Wheel_Orientation_" + number, val);
            }
        }
    }

    /**
     * invoke updating the actual values and the motor outputs called
     * automatically from setDesired()
     * 
     * @return Actual vector reading of wheel
     */
    private Vector updateTask()
    {
        boolean reverse = updateAngleMotor();

        updateShifter();

        updateDriveMotor(reverse);

        // SmartDashboard.putNumber(Name + ".desired.mag",
        // WheelDesired.getMag());
        // SmartDashboard.putNumber(Name + ".desired.ang",
        // WheelDesired.getAngle());

        //SmartDashboard.putNumber(name + ".angle.noOffset", anglePot.getWithoutOffset());
        //SmartDashboard.putNumber(name + ".angle.raw", anglePot.getRawInput());
        
        return getActual();
    }

    /**
     * Update the angle motor based on the desired angle Called from
     * updateTask()
     * 
     * @return Whether the drive motor should run in the opposite direction
     */
    private boolean updateAngleMotor()
    {
        // update the offsets
        autoCalibration();
        updateAngleOffset();
        updateMaxRotationSpeed();

        double des = desired.getAngle();
        double sensor = getAnglePotAdjusted();
        // Update the angle controller.
        anglePID.update(des, sensor);

        double error = anglePID.error;
        
        SmartDashboard.putNumber(name+".angle.raw", anglePot.getRawInput());
        
        if (desired.getMag() > MINIMUM_SPEED)
        {
            // Control the wheel angle.
            angleMotor.set(anglePID.getMotorSpeed() * angleMaxSpeed);
        }
        else
        {
            // Too slow, do nothing
            anglePID.resetIntegral();
            angleMotor.set(0);
        }
        SmartDashboard.putNumber(name + ".angle.des", des);
        SmartDashboard.putNumber(name + ".angle", sensor);
        SmartDashboard.putNumber(name + ".angle.err", error);

        return anglePID.isReverseMotor();
    }

    /**
     * Update the drive motor based on the desired speed and whether to run in
     * reverse Called from updateTask()
     */
    private void updateDriveMotor(boolean reverse)
    {
        
        // Control the wheel speed.
        double driveMotorSpeed = desired.getMag();

        // Reverse the motor output if the angle controller is taking advantage
        // of rotational symmetry.
        if (reverse)
        {
            driveMotorSpeed = -driveMotorSpeed;
        }

        // limit the ramp rate to prevent voltage drops
        // and brownouts
        if(!SwerveConstants.DRIVE_MOTOR_SPEED_SENSOR_ENABLE)
        {
            // we don't have speed feedback, so brute force it
            // using the desired and the last desired values
            double diff = Math.abs(driveLastSpeed - driveMotorSpeed);
            
            if(diff > .5)
            {
                driveMotor.setVoltageRampRate(8);
                driveLastChangeTime = Timer.getFPGATimestamp();
                if(this.number == 0)
                    SmartDashboard.putNumber("VoltageRampRate", 8);
            }
            else if(diff < .35 && (Timer.getFPGATimestamp() - driveLastChangeTime > .25))
            {
                driveMotor.setVoltageRampRate(30);
                if(this.number == 0)
                    SmartDashboard.putNumber("VoltageRampRate", 30);
            }

            driveLastSpeed = driveMotorSpeed;
        }
        else
        {
            // we have a speed sensor, so 
            double speedCurrent = driveMotor.getSpeed();
            
            double speedDesired = driveMotorSpeed * SwerveConstants.DriveMaxSpeed;
            
            double speedDiff = Math.abs(speedDesired-speedCurrent);
            
            // limit to 0 - max
            speedDiff = Math.min(speedDiff, SwerveConstants.DriveMaxSpeed);
            
            // linearly scale the speed difference to the ramp range
            //TODO should it be linear?
            double rampValue = Utilities.scaleToRange(speedDiff,
                    0, SwerveConstants.DriveMaxSpeed, // input range
                    SwerveConstants.DRIVE_RAMP_LOW, SwerveConstants.DRIVE_RAMP_HIGH); // output range

            driveMotor.setVoltageRampRate(rampValue);
            SmartDashboard.putNumber("VoltageRampRate", rampValue);
            
            driveLastSpeed = driveMotorSpeed;
        }

        double driveMotorOutput = 0;
        // don't try and drive if it's below the friction limit
        if(Math.abs(driveMotorSpeed) >= SwerveConstants.DRIVE_MOTOR_SPEED_MIN)
        {
            driveMotorOutput = driveMotorSpeed;
        }

        if (brake)
        {
            driveMotor.set(0);
            driveMotor.enableBrakeMode(true);
        }
        else
        {
            driveMotor.enableBrakeMode(false);
            driveMotor.set(driveMotorOutput);
        }

        // SmartDashboard.putNumber(Name + ".position.raw", DriveEncoder.getRaw());
        // SmartDashboard.putNumber(Name + ".position.scaled", DriveEncoder.getDistance());
        // SmartDashboard.putNumber(Name + ".speed.filtered", DriveEncoderFilter.getVelocity());
        // SmartDashboard.putNumber(Name + ".speed.scaled", driveEncoderVelocityScaled);
        // SmartDashboard.putNumber(Name + ".speed.error", driveMotorControllerError);
        // SmartDashboard.putNumber(Name + ".speed.adjust", driveMotorControllerOutput);
        SmartDashboard.putNumber(name + ".speed.motor", driveMotorOutput);
    }

    protected void updateAngleOffset()
    {
        anglePot.setOffset(Preferences.getInstance().getDouble(
                "Wheel_Orientation_" + number,
                SwerveConstants.WheelOrientationAngle[number]));
    }

    protected void updateMaxRotationSpeed()
    {
        angleMaxSpeed = Preferences.getInstance()
                .getDouble("maxRotationSpeed", angleMaxSpeed);
    }
}
