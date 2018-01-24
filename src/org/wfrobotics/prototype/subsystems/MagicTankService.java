package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.reuse.subsystems.tank.TankService;

public class MagicTankService extends TankService
{
    private static MagicTankService instance = null;
    private MagicTankSubsystem drive;

    private MagicTankService()
    {
        drive = MagicTankSubsystem.getInstance();
    }

    public static MagicTankService getInstance()
    {
        if (instance == null)
        {
            instance = new MagicTankService();
        }
        return instance;
    }
}
