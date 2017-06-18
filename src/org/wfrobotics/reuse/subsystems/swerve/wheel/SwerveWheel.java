package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.Vector;
import org.wfrobotics.reuse.subsystems.swerve.Shifter;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Handle motor outputs and feedback for an individual swerve wheel
 * @author Team 4818 WFRobotics
 */
public class SwerveWheel
{
    public final Vector POSITION_RELATIVE_TO_CENTER;
    private final String NAME;
    private final int NUMBER;

    private final DriveMotor driveManager;
    private final AngleMotor angleManager;
    private final Shifter shifter;

    public SwerveWheel(String name, int number, Vector position)
    {
        NAME = name;
        this.NUMBER = number;
        this.POSITION_RELATIVE_TO_CENTER = position;

        driveManager = new DriveMotor(RobotMap.CAN_SWERVE_DRIVE_TALONS[NUMBER], Constants.DRIVE_SPEED_SENSOR_ENABLE);
        angleManager = new AngleMotorMagPot(NAME + ".Angle", RobotMap.CAN_SWERVE_ANGLE_TALONS[NUMBER]);
        shifter = new Shifter(RobotMap.PWM_SWERVE_SHIFT_SERVOS[NUMBER], Constants.SHIFTER_VALS[NUMBER],
                              Constants.SHIFTER_RANGE, Constants.SHIFTER_INVERT[NUMBER]);
    }
    
    /**
     * Set the desired wheel vector, auto updates the PID controllers
     * @param desired Velocity and Rotation of this wheel
     * @param gearHigh True: High gear, False: Low gear
     * @param brake Enable brake mode?
     * @return Actual vector of wheel (sensor feedback)
     */
    public Vector set(Vector desired, boolean gear, boolean brake)
    {
        boolean reverseDrive = angleManager.update(desired);  // TODO Consider moving motor reversal to swerve wheel. Is it a "wheel thing" or "angle motor thing"?
        shifter.setGear(gear);
        
        double driveCommand = (reverseDrive) ? -desired.getMag() : desired.getMag();
        driveManager.set(driveCommand, brake);
        //SmartDashboard.putNumber(name + ".speed.motor", driveMotorOutput);

        return getActual(desired);
    }

    public Vector getActual(Vector desiredVector)
    {
        double magnitude = (Constants.DRIVE_SPEED_SENSOR_ENABLE) ? driveManager.get()/Constants.DRIVE_SPEED_MAX : desiredVector.getMag();

        return Vector.NewFromMagAngle(magnitude, angleManager.getDegrees());
    }

    public void printDash()
    {
        SmartDashboard.putNumber(NAME + ".angle", angleManager.getDegrees());
        SmartDashboard.putNumber("SpeedCurrent" + NUMBER, driveManager.get());
    }
}        
