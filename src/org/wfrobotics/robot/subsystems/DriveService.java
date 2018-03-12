package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveService implements org.wfrobotics.reuse.subsystems.drive.DriveService
{
    private static DriveService instance = null;
    public DriveSubsystem drive;

    protected DriveService(RobotConfig config)
    {
        drive = DriveSubsystem.getInstance();
    }

    public static DriveService getInstance()
    {
        if (instance == null)
        {
            instance = new DriveService(Robot.config);
        }
        return instance;
    }

    public Subsystem getSubsystem()
    {
        return drive;
    }

    public void driveBasic(HerdVector vector)
    {
        drive.driveBasic(vector);
    }

    public void turnToHeading(double fieldRelativeAngle)
    {
        drive.turnToHeading(fieldRelativeAngle);
    }

    public void driveDistanceInit(double inchesForward)
    {
        drive.driveDistanceInit(inchesForward);
    }

    public void driveDistanceUpdate()
    {
        drive.driveDistanceUpdate();
    }

    public void drivePath()
    {
        drive.drivePath();
    }

    public boolean onTarget()
    {
        return drive.onTarget();
    }

    public void setBrake(boolean enable)
    {
        drive.setBrake(enable);
    }

    public void setGear(boolean useHighGear)
    {
        drive.setGear(useHighGear);
    }

    public void zeroGyro()
    {
        drive.zeroGyro();
    }

    public void driveDifferential(double left, double right)
    {
        drive.driveDifferential(left, right);
    }

    public void driveWithHeading(SwerveSignal command)
    {
        DriverStation.reportError(String.format("%s does not support drive command", getClass().getSimpleName()), true);
    }
}
