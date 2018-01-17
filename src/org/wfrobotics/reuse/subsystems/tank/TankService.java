package org.wfrobotics.reuse.subsystems.tank;

import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;

import edu.wpi.first.wpilibj.DriverStation;

public class TankService implements DriveService<TankSubsystem>
{
    private static TankService instance = null;
    private TankSubsystem drive;

    public TankService()
    {
        drive = TankSubsystem.getInstance();
    }

    public static TankService getInstance()
    {
        if (instance == null)
        {
            instance = new TankService();
        }
        return instance;
    }

    public TankSubsystem getSubsystem()
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
        drive.driveDifferential(left, right);
    }

    public void driveWithHeading(SwerveSignal command)
    {
        DriverStation.reportError(String.format("%s does not support drive command", getClass().getSimpleName()), true);
    }
}
