package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.subsystems.drive.HolonomicService;

public class SwerveService implements HolonomicService<SwerveSubsystem>
{
    private static SwerveService instance = null;
    private SwerveSubsystem drive;

    public SwerveService(SwerveSubsystem holonomicDriveSubsystem)
    {
        drive = holonomicDriveSubsystem;
    }

    public static SwerveService getInstance()
    {
        if (instance == null)
        {
            instance = new SwerveService(SwerveSubsystem.getInstance());
        }
        return instance;
    }

    public SwerveSubsystem getDrive()
    {
        return drive;
    }
}
