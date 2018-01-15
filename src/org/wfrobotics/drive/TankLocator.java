package org.wfrobotics.drive;

public class TankLocator implements DifferentialLocator<TankSubsystem>
{
    private static TankLocator instance = null;
    private TankSubsystem drive;

    public TankLocator(TankSubsystem differentialDriveSubsystem)
    {
        drive = differentialDriveSubsystem;
    }

    public static TankLocator getInstance()
    {
        if (instance == null)
        {
            instance = new TankLocator(TankSubsystem.getInstance());
        }
        return instance;
    }

    public TankSubsystem getDrive()
    {
        return drive;
    }
}
