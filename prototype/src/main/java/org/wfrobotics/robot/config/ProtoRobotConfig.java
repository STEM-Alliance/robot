package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.EnhancedRobotConfig;
import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TankConfig;

public class ProtoRobotConfig extends EnhancedRobotConfig
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
            instance = (ProtoRobotConfig) RobotConfigPicker.get(new EnhancedRobotConfig[] {
                new ProtoRobotConfig(), // Competition robot
            });
        }
        return instance;
    }
}

