package org.wfrobotics.reuse.subsystems.tank;

import org.wfrobotics.reuse.subsystems.drive.DifferentialService;

public class TankService implements DifferentialService<TankSubsystem>
{
    private static TankService instance = null;
    private TankSubsystem drive;

    public TankService(TankSubsystem differentialDriveSubsystem)
    {
        drive = differentialDriveSubsystem;
    }

    public static TankService getInstance()
    {
        if (instance == null)
        {
            instance = new TankService(TankSubsystem.getInstance());
        }
        return instance;
    }

    public TankSubsystem getDrive()
    {
        return drive;
    }
}
