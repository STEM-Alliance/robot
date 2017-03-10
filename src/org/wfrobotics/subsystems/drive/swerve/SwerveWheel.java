package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.robot.RobotMap;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Handle motor outputs and feedback for an individual swerve wheel * 
 * @author Team 4818 WFRobotics
 */
public class SwerveWheel
{
    public String name;
    public int number;

    /** Wheel location from center of robot */
    public final Vector position;
    /** Actual wheel vector, from sensors */
    private Vector actualVector;

    private final WheelDriveManager driveManager;
    private final WheelAngleManager angleManager;
    private final Shifter shifter;
    
    private Vector desiredVector;
    private boolean desiredBrake;
    private boolean desiredGear;

    /** Drive motor object */
    private double driveLastSpeed;
    private double driveLastChangeTime;
    
    /** Max speed the rotation can spin, relative to motor maximum */
    private double angleMaxSpeed = -.7;

    /** Minimum speed, used for dead band */ 
    protected static final double MINIMUM_SPEED = 0.1;
    
    private double lastUpdateTime = 0;

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
        actualVector = new Vector(0, 0);
        desiredVector = new Vector(0, 0);

        // set up the drive motor
        driveManager = new WheelDriveManager(RobotMap.CAN_SWERVE_DRIVE_TALONS[number], SwerveConstants.DRIVE_SPEED_SENSOR_ENABLE);
        driveLastChangeTime = Timer.getFPGATimestamp();
        //driveMotor.setCurrentLimit(5);

        angleManager = new WheelAngleManager(name + ".ctl", RobotMap.CAN_SWERVE_ANGLE_TALONS[number]);

        //angleCalSensor = new DigitalInput(RobotMap.DIO_SWERVE_CAL[number]);

        shifter = new Shifter(RobotMap.PWM_SWERVE_SHIFT_SERVOS[number], SwerveConstants.SHIFTER_VALS[number][0], SwerveConstants.SHIFTER_VALS[number][1]);
        desiredGear = false;
        
        lastUpdateTime = Timer.getFPGATimestamp();
    }
    
    /**
     * Set the desired wheel vector, auto updates the PID controllers
     * @param desiredVector Velocity and Rotation of this wheel instance
     * @param gearHigh True: High, False: Low
     * @return Actual vector of wheel (sensor feedback)
     */
    public Vector setDesired(Vector desiredVector, boolean desiredgear, boolean desiredBrake)
    {
        boolean reverseDriveMotors;
        
        // Store desired values
        this.desiredVector = desiredVector;
        this.desiredGear = desiredgear;
        this.desiredBrake = desiredBrake;

        // Set the hardware based on the new desired values
        reverseDriveMotors = updateAngleMotor();
        shifter.setGear(this.desiredGear);
        updateDriveMotor(reverseDriveMotors);

        SmartDashboard.putNumber(name+"UpdateRate", Timer.getFPGATimestamp() - lastUpdateTime);
        lastUpdateTime = Timer.getFPGATimestamp();
        return getActual();
    }

    /**
     * Get the actual vector reading of the wheel
     * @return Actual vector reading of wheel
     */
    public Vector getActual()
    {
        double magnitude = (SwerveConstants.DRIVE_SPEED_SENSOR_ENABLE) ? driveManager.get()/SwerveConstants.DRIVE_MAX_SPEED:desiredVector.getMag();
        
        actualVector.setMagAngle(magnitude, angleManager.getAnglePotAdjusted());
        
        return actualVector;
    }

    /**
     * Update the angle motor based on the desired angle Called from
     * updateTask()
     * 
     * @return Whether the drive motor should run in the opposite direction
     */
    private boolean updateAngleMotor()
    {
        double error;
        
        updateAngleOffset();
        updateMaxRotationSpeed();

        double setpoint = desiredVector.getAngle();
        double current = angleManager.getAnglePotAdjusted();
        
        angleManager.update(setpoint, current);
        error = angleManager.error;
        
        //SmartDashboard.putNumber(name+".angle.raw", angleManager.debugGetPotRaw());
        if (desiredVector.getMag() > MINIMUM_SPEED)
        {
            angleManager.set(angleManager.getSpeed() * angleMaxSpeed);  // Control the wheel angle.
        }
        else
        {
            // Too slow, do nothing
            angleManager.resetIntegral();
            angleManager.set(0);
        }
        
        //SmartDashboard.putNumber(name + ".angle.des", setpoint);
        //SmartDashboard.putNumber(name + ".angle", current);
        SmartDashboard.putNumber(name + ".angle.err", error);

        return angleManager.isReverseMotor();
    }

    /**
     * Update the drive motor based on the desired speed and whether to run in
     * reverse Called from updateTask()
     */
    private void updateDriveMotor(boolean reverse)
    {
        // Control the wheel speed.
        double driveMotorSpeed = desiredVector.getMag();
        double driveMotorOutput = 0;

        // Reverse the output if the angle PID can take advantage of rotational symmetry
        driveMotorSpeed = (reverse) ? -driveMotorSpeed:driveMotorSpeed;
        
        // Limit ramp rate, prevents voltage drops and brownouts
        if(!SwerveConstants.DRIVE_SPEED_SENSOR_ENABLE)
        {
            // We don't have speed feedback, so brute force it using the desired and the last desired values
            double diff = Math.abs(driveLastSpeed - driveMotorSpeed);
            
            if(diff > .5)
            {
                //driveManager.setVoltageRampRate(8);
                driveLastChangeTime = Timer.getFPGATimestamp();
                //if(this.number == 0)
                    //SmartDashboard.putNumber("VoltageRampRate", 8);
            }
            else if(diff < .35 && (Timer.getFPGATimestamp() - driveLastChangeTime > .40))
            {
                //driveManager.setVoltageRampRate(15);
                //if(this.number == 0)
                    //SmartDashboard.putNumber("VoltageRampRate", 15);
            }

            driveLastSpeed = driveMotorSpeed;
            driveMotorOutput = driveMotorSpeed;
        }
        else
        {
            // We have a speed sensor, so 
            double speedCurrent = driveManager.get();

            //SmartDashboard.putNumber("SpeedCurrent" + number, speedCurrent);
            //SmartDashboard.putNumber("SpeedInput" + number, driveMotorSpeed);
            driveMotorSpeed = driveMotorSpeed * SwerveConstants.DRIVE_MAX_SPEED;
            //SmartDashboard.putNumber("SpeedOutput" + number, driveMotorSpeed);

            // limit to 0 - max
            double speedDiff = Math.min(Math.abs(driveMotorSpeed-speedCurrent), SwerveConstants.DRIVE_MAX_SPEED);
                        
            // linearly scale the speed difference to the ramp range, //TODO should it be linear?
            double rampValue = Utilities.scaleToRange(speedDiff, 0, SwerveConstants.DRIVE_MAX_SPEED, // input range
                    SwerveConstants.DRIVE_RAMP_HIGH, SwerveConstants.DRIVE_RAMP_LOW); // output range

            //driveMotor.setCloseLoopRampRate(rampValue);
            //SmartDashboard.putNumber("VoltageRampRate" + number, rampValue);
            //SmartDashboard.putNumber("SpeedDiff" + number, speedDiff);
            
            driveMotorOutput = driveMotorSpeed;
            driveLastSpeed = driveMotorSpeed;
        }
        
        // If braking is requested
        driveMotorOutput = (desiredBrake) ? 0:driveMotorOutput;        
        driveManager.set(driveMotorOutput);
        // WARNING: Setting brake mode each iteration drastically decreases performance driveManager.setBrake(desiredBrake);

        //SmartDashboard.putNumber(name + ".speed.motor", driveMotorOutput);
    }

    public void updateAngleOffset()
    {
        updateAngleOffset(Preferences.getInstance().getDouble("Wheel_Orientation_" + number,
                          SwerveConstants.ANGLE_OFFSET[number]));
    }

    /**
     * Test with the specified value as the angle offset.
     * @param angleOffset angle offset in degrees
     */
    public void updateAngleOffset(double angleOffset)
    {
        //double savedAngle = Preferences.getInstance().getDouble("Wheel_Orientation_" + number,
        //        SwerveConstants.ANGLE_OFFSET[number]);
        
        angleManager.setPotOffset(angleOffset);
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

    public void free()
    {
        //try { gearShifter.free(); } catch (AllocationException e) {}
        angleManager.free();
    }

    public void printDash()
    {
        SmartDashboard.putNumber(name + ".angle", angleManager.getAnglePotAdjusted());
        SmartDashboard.putNumber("SpeedCurrent" + number, driveManager.get());
    }

    public double getAngleOffset()
    {
        return Preferences.getInstance().getDouble("Wheel_Orientation_" + number, SwerveConstants.ANGLE_OFFSET[number]);
    }
}