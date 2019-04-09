package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.TankConfig;

/** Practice Robot Config. Override any settings that differ here */
public final class PracticeConfig extends RobotConfig
{
    public PracticeConfig()
    {
        super();
    }

    public TankConfig getTankConfig()
    {
        TankConfig config = new TankConfig();

        return config;
    }
}
