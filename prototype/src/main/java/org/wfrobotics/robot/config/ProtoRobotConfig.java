package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.IRobotConfig;
import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.config.TankConfig;
import org.wfrobotics.reuse.config.TankConfig.TankConfigSupplier;

public class ProtoRobotConfig implements TankConfigSupplier, IRobotConfig
{
    private static ProtoRobotConfig instance = null;


    //                      Tank
    // _________________________________________________________________________________

    // Hardware
    public TankConfig getTankConfig()
    {
        return new TankConfig();
    }

    public class ProtoTankConfig extends TankConfig
    {

    }

    //                      Helper Methods
    // _________________________________________________________________________________

    public static ProtoRobotConfig getInstance()
    {
        if (instance == null)
        {
            instance = (ProtoRobotConfig) RobotConfigPicker.get(new IRobotConfig[] {
                new ProtoRobotConfig(), // Competition robot
            });
        }
        return instance;
    }
}

