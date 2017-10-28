package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;

/**
 * Handle motor outputs and feedback for an individual swerve wheel
 * @author Team 4818 WFRobotics
 */
public class SwerveWheel
{
    RobotState state = RobotState.getInstance();

    private final DriveMotor driveMotor;
    private final AngleMotor angleMotor;

    public SwerveWheel(DriveMotor motorDrive, AngleMotor motorAngle)
    {
        driveMotor = motorDrive;
        angleMotor = motorAngle;
    }

    public String toString()
    {
        return String.format("%.2f, %.2f\u00b0", driveMotor.get(), angleMotor.getDegrees());
    }

    /**
     * Set the desired wheel vector, auto updates the PID controllers
     * @param desired Velocity and Rotation of this wheel
     * @param brake Enable brake mode?
     */
    public void set(HerdVector desired, boolean brake)
    {
        boolean reverseDrive = angleMotor.update(desired);  // TODO Consider moving motor reversal to swerve wheel. Is it a "wheel thing" or "angle motor thing"?

        double driveCommand = (reverseDrive) ? -desired.getMag() : desired.getMag();
        driveMotor.set(driveCommand);
        driveMotor.setBrake(brake);
    }
}
