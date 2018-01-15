package org.wfrobotics.drive;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSubsystem;

public class SwerveLocator implements HolonomicLocator<SwerveSubsystem>
{
    private static SwerveLocator instance = null;
    private SwerveSubsystem drive;

    public SwerveLocator(SwerveSubsystem holonomicDriveSubsystem)
    {
        drive = holonomicDriveSubsystem;
    }

    public static SwerveLocator getInstance()
    {
        if (instance == null)
        {
            instance = new SwerveLocator(SwerveSubsystem.getInstance());
        }
        return instance;
    }

    public SwerveSubsystem getDrive()
    {
        return drive;
    }
}
