package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.reuse.utilities.HerdVector;

import edu.wpi.first.wpilibj.DriverStation;

public class SwerveService implements DriveService<SwerveSubsystem>
{
    private static SwerveService instance = null;
    private SwerveSubsystem drive;

    public SwerveService()
    {
        drive = SwerveSubsystem.getInstance();
    }

    public static SwerveService getInstance()
    {
        if (instance == null)
        {
            instance = new SwerveService();
        }
        return instance;
    }

    public SwerveSubsystem getSubsystem()
    {
        return drive;
    }

    public void driveBasic(HerdVector vector)
    {
        drive.driveBasic(vector);
    }

    public void turnBasic(HerdVector vector)
    {
        drive.turnBasic(vector);
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
        DriverStation.reportError(String.format("%s does not support drive command", getClass().getSimpleName()), true);
    }

    public void driveWithHeading(SwerveSignal command)
    {
        drive.driveWithHeading(command);
    }
}
