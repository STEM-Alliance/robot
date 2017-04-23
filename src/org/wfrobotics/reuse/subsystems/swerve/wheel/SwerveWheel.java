package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.reuse.subsystems.swerve.Shifter;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Handle motor outputs and feedback for an individual swerve wheel
 * @author Team 4818 WFRobotics
 */
public class SwerveWheel
{
    protected static final double DEADBAND_MINIMUM_SPEED = 0.1;
    /** Max speed the rotation can spin, relative to motor maximum */
    private double ANGLE_MAX_SPEED = -.7;
    public final Vector POSITION_RELATIVE_TO_CENTER;
    private final String NAME;
    private final int NUMBER;

    private final DriveMotor driveManager;
    private final AngleMotor angleManager;
    private final AngleController anglePID;
    private final Shifter shifter;
    
    private Vector desiredVector;
    private boolean desiredBrake;
    private boolean desiredGear;
    private Vector actualVector;
    
    private double driveLastSpeed;
    private double driveLastChangeTime;
    private double lastUpdateTime = 0;

    public SwerveWheel(String name, int Number, Vector position)
    {
        NAME = name;
        this.NUMBER = Number;
        this.POSITION_RELATIVE_TO_CENTER = position;

        driveManager = new DriveMotor(RobotMap.CAN_SWERVE_DRIVE_TALONS[NUMBER], Constants.DRIVE_SPEED_SENSOR_ENABLE);
        angleManager = new AngleMotorMagPot(RobotMap.CAN_SWERVE_ANGLE_TALONS[NUMBER]);
        anglePID = new AngleController(NAME + ".ctl");
        shifter = new Shifter(RobotMap.PWM_SWERVE_SHIFT_SERVOS[NUMBER], Constants.SHIFTER_VALS[NUMBER],
                              Constants.SHIFTER_RANGE, Constants.SHIFTER_INVERT[NUMBER]);
        
        actualVector = new Vector(0, 0);
        desiredVector = new Vector(0, 0);
        desiredBrake = false;
        desiredGear = false;
    
        driveLastSpeed = 0;
        driveLastChangeTime = Timer.getFPGATimestamp();
        lastUpdateTime = Timer.getFPGATimestamp();
    }
    
    public String toString()
    {
        return NAME;
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
        
        this.desiredVector = desiredVector;
        this.desiredGear = desiredgear;
        this.desiredBrake = desiredBrake;

        reverseDriveMotors = updateAngleMotor();
        shifter.setGear(this.desiredGear);
        updateDriveMotor(reverseDriveMotors);

        SmartDashboard.putNumber(NAME+"UpdateRate", Timer.getFPGATimestamp() - lastUpdateTime);
        lastUpdateTime = Timer.getFPGATimestamp();

        return getActual();
    }

    public Vector getActual()
    {
        double magnitude = (Constants.DRIVE_SPEED_SENSOR_ENABLE) ? driveManager.get()/Constants.DRIVE_SPEED_MAX : desiredVector.getMag();
        
        actualVector.setMagAngle(magnitude, angleManager.getAnglePotAdjusted());
        
        return actualVector;
    }

    /**
     * Update the angle motor based on the desired angle called from updateTask()
     * @return Whether the drive motor should run in the opposite direction
     */
    private boolean updateAngleMotor()
    {
        updateAngleOffset();
        ANGLE_MAX_SPEED = Preferences.getInstance().getDouble("maxRotationSpeed", ANGLE_MAX_SPEED);

        double setpoint = desiredVector.getAngle();
        double current = angleManager.getAnglePotAdjusted();
        
        anglePID.update(setpoint, current);
        double error = anglePID.error;
        
        //SmartDashboard.putNumber(name+".angle.raw", angleManager.debugGetPotRaw());
        if (desiredVector.getMag() > DEADBAND_MINIMUM_SPEED)
        {
            angleManager.set(anglePID.getMotorSpeed() * ANGLE_MAX_SPEED);
        }
        else
        {
            anglePID.resetIntegral();
            angleManager.set(0);
        }
        
        //SmartDashboard.putNumber(name + ".angle.des", setpoint);
        SmartDashboard.putNumber(NAME + ".angle", current);
        SmartDashboard.putNumber(NAME + ".angle.err", error);

        return anglePID.isReverseMotor();
    }

    private void updateDriveMotor(boolean reverse)
    {
        double driveMotorSpeed = desiredVector.getMag();
        double driveMotorOutput = 0;

        // Reverse the output if the angle PID says that is the shortest path
        driveMotorSpeed = (reverse) ? -driveMotorSpeed : driveMotorSpeed;
        
        // Limit ramp rate, prevents voltage drops and brownouts
        if(!Constants.DRIVE_SPEED_SENSOR_ENABLE)
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
            double speedCurrent = driveManager.get();

            //SmartDashboard.putNumber("SpeedCurrent" + number, speedCurrent);
            //SmartDashboard.putNumber("SpeedInput" + number, driveMotorSpeed);
            driveMotorSpeed *= Constants.DRIVE_SPEED_MAX;
            //SmartDashboard.putNumber("SpeedOutput" + number, driveMotorSpeed);

            // Limit to 0 - max
            double speedDiff = Math.min(Math.abs(driveMotorSpeed - speedCurrent), Constants.DRIVE_SPEED_MAX);
                        
            // Linearly scale the speed difference to the ramp range, //TODO should it be linear?
            double rampValue = Utilities.scaleToRange(speedDiff, 0, Constants.DRIVE_SPEED_MAX, Constants.DRIVE_RAMP_HIGH, Constants.DRIVE_RAMP_LOW); // output range

            //driveMotor.setCloseLoopRampRate(rampValue);
            //SmartDashboard.putNumber("VoltageRampRate" + number, rampValue);
            //SmartDashboard.putNumber("SpeedDiff" + number, speedDiff);
            
            driveMotorOutput = driveMotorSpeed;
            driveLastSpeed = driveMotorSpeed;
        }
        
        // If braking is requested
        driveMotorOutput = (desiredBrake) ? 0 : driveMotorOutput;
        driveManager.set(driveMotorOutput);
        // WARNING: Setting brake mode each iteration drastically decreases performance driveManager.setBrake(desiredBrake);

        //SmartDashboard.putNumber(name + ".speed.motor", driveMotorOutput);
    }

    public void updateAngleOffset()
    {
        updateAngleOffset(Preferences.getInstance().getDouble("Wheel_Orientation_" + NUMBER, 0));
    }

    public void updateAngleOffset(double angleOffset)
    {
        angleManager.setPotOffset(angleOffset);
    }
    
    /**
     * Save the specified value as the angle offset
     * @param value angle offset in degrees
     */
    public void saveAngleOffset(double value)
    {
        Preferences.getInstance().putDouble("Wheel_Orientation_" + NUMBER, value);
    }
    
    public double getAngleOffset()
    {
        return Preferences.getInstance().getDouble("Wheel_Orientation_" + NUMBER, 0);
    }

    public void free()
    {
        angleManager.free();
    }

    public void printDash()
    {
        SmartDashboard.putNumber(NAME + ".angle", angleManager.getAnglePotAdjusted());
        SmartDashboard.putNumber("SpeedCurrent" + NUMBER, driveManager.get());
    }
}