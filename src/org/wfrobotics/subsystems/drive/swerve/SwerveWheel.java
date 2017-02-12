package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.hardware.*;
import org.wfrobotics.robot.RobotMap;

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

    /** Minimum speed, used for dead band */ 
    private static final double MINIMUM_SPEED = 0.1;

    /**
     * Set up a swerve wheel using controllers/objects,
     * using an SRX input for angle
     * @param Number
     */
    public SwerveWheel(int Number)
    {
        // store some info off the start
        name = "Wheel" + Number;
        this.number = Number;

        position = new Vector(SwerveConstants.POSITIONS[number]);
        actual = new Vector(0, 0);
        desired = new Vector(0, 0);

        // set up the drive motor
        driveMotor = new CANTalon(RobotMap.CAN_SWERVE_DRIVE_TALONS[number]);

        driveMotor.setVoltageRampRate(20);
        driveLastChangeTime = Timer.getFPGATimestamp();
        //driveMotor.setCurrentLimit(5);
        driveMotor.ConfigFwdLimitSwitchNormallyOpen(true);
        driveMotor.ConfigRevLimitSwitchNormallyOpen(true);
        driveMotor.enableForwardSoftLimit(false);
        driveMotor.enableReverseSoftLimit(false);
        driveMotor.enableBrakeMode(false);
        
        // if we have it configured to use a speed sensor, setup that too
        if(SwerveConstants.DRIVE_SPEED_SENSOR_ENABLE)
        {
            driveMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            driveMotor.changeControlMode(TalonControlMode.Speed);
            driveMotor.setPID(SwerveConstants.DRIVE_PID_P,
                              SwerveConstants.DRIVE_PID_I,
                              SwerveConstants.DRIVE_PID_D,
                              SwerveConstants.DRIVE_PID_F,
                              0,
                              10,
                              0);
            driveMotor.reverseSensor(true);
        }

        // setup the angle motor
        angleMotor = new CANTalon(RobotMap.CAN_SWERVE_ANGLE_TALONS[number]);
        angleMotor.setVoltageRampRate(20);
        angleMotor.ConfigFwdLimitSwitchNormallyOpen(true);
        angleMotor.ConfigRevLimitSwitchNormallyOpen(true);
        angleMotor.enableForwardSoftLimit(false);
        angleMotor.enableReverseSoftLimit(false);
        angleMotor.enableBrakeMode(false);

        // setup the angle sensor to use the SRX port
        anglePot = new MagnetoPotSRX(angleMotor, 360);
        anglePID = new SwerveAngleController(name + ".ctl");

        //angleCalSensor = new DigitalInput(RobotMap.DIO_SWERVE_CAL[number]);

        // setup high gear
        gearHigh = true;
        this.gearShifter = new Servo(RobotMap.PWM_SWERVE_SHIFT_SERVOS[number]);
        gearShifterAngle = SwerveConstants.SHIFTER_VALS[number];
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
        return Utilities.round(Utilities.wrapToRange(invert * anglePot.get(),-180,180),2);
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
        if(SwerveConstants.DRIVE_SPEED_SENSOR_ENABLE)
        {
            actual.setMagAngle(driveMotor.getSpeed()/SwerveConstants.DRIVE_MAX_SPEED, getAnglePotAdjusted());
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

    /**
     * Needed to be called to change the actual shifter
     */
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
     * TODO
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

        // don't try and drive if it's below the friction limit
        if(Math.abs(driveMotorSpeed) < SwerveConstants.DRIVE_SPEED_MIN)
        {
            driveMotorSpeed = 0;
        }
        
        double driveMotorOutput = 0;
        
        // limit the ramp rate to prevent voltage drops
        // and brownouts
        if(!SwerveConstants.DRIVE_SPEED_SENSOR_ENABLE)
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
            else if(diff < .35 && (Timer.getFPGATimestamp() - driveLastChangeTime > .40))
            {
                driveMotor.setVoltageRampRate(15);
                if(this.number == 0)
                    SmartDashboard.putNumber("VoltageRampRate", 15);
            }

            driveLastSpeed = driveMotorSpeed;
            driveMotorOutput = driveMotorSpeed;
            
        }
        else
        {
            // we have a speed sensor, so 
            double speedCurrent = driveMotor.getSpeed();

            SmartDashboard.putNumber("SpeedCurrent" + number, speedCurrent);
            SmartDashboard.putNumber("SpeedInput" + number, driveMotorSpeed);
            driveMotorSpeed = driveMotorSpeed * SwerveConstants.DRIVE_MAX_SPEED;
            SmartDashboard.putNumber("SpeedOutput" + number, driveMotorSpeed);
            
            double speedDiff = Math.abs(driveMotorSpeed-speedCurrent);
            
            // limit to 0 - max
            speedDiff = Math.min(speedDiff, SwerveConstants.DRIVE_MAX_SPEED);
            
            // linearly scale the speed difference to the ramp range
            //TODO should it be linear?
            double rampValue = Utilities.scaleToRange(speedDiff,
                    0, SwerveConstants.DRIVE_MAX_SPEED, // input range
                    SwerveConstants.DRIVE_RAMP_HIGH, SwerveConstants.DRIVE_RAMP_LOW); // output range

            //driveMotor.setCloseLoopRampRate(rampValue);
            SmartDashboard.putNumber("VoltageRampRate" + number, rampValue);
            SmartDashboard.putNumber("SpeedDiff" + number, speedDiff);
            
            driveMotorOutput = driveMotorSpeed;
            driveLastSpeed = driveMotorSpeed;
        }

        if(number == 0)
        {
            SmartDashboard.putNumber("CLRampRate" + number, driveMotor.getCloseLoopRampRate());
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

        SmartDashboard.putNumber(name + ".speed.motor", driveMotorOutput);
    }

    public void updateAngleOffset()
    {
        updateAngleOffset(Preferences.getInstance().getDouble("Wheel_Orientation_" + number,
                          SwerveConstants.ANGLE_OFFSET[number]));
    }

    /**
     * Test with the specified value as the angle offset.
     * @param value angle offset in degrees
     */
    public void updateAngleOffset(double value)
    {
        //double savedAngle = Preferences.getInstance().getDouble("Wheel_Orientation_" + number,
        //        SwerveConstants.ANGLE_OFFSET[number]);
        
        anglePot.setOffset(value);
    }
    
    /**
     * Save the specified value as the angle offset.
     * Note: this will add the specified value to the already existing offset
     * @param value angle offset in degrees
     */
    public void saveAngleOffset(double value)
    {
        //double savedAngle = Preferences.getInstance().getDouble("Wheel_Orientation_" + number,
        //        SwerveConstants.ANGLE_OFFSET[number]);

        Preferences.getInstance().putDouble("Wheel_Orientation_" + number, value);
    }

    protected void updateMaxRotationSpeed()
    {
        angleMaxSpeed = Preferences.getInstance()
                .getDouble("maxRotationSpeed", angleMaxSpeed);
    }

    public void printDash()
    {
        SmartDashboard.putNumber(name + ".angle", getAnglePotAdjusted());
        SmartDashboard.putNumber("SpeedCurrent" + number, driveMotor.getSpeed());
    }

    public double getAngleOffset()
    {
        return Preferences.getInstance().getDouble("Wheel_Orientation_" + number, SwerveConstants.ANGLE_OFFSET[number]);
    }
}
