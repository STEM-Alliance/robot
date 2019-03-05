package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;

public final class Robot extends EnhancedRobot
{
    public Robot()
    {
        super(RobotConfig.getInstance(),RobotState.getInstance(), IO.getInstance());
    }

    protected void registerRobotSpecific()
    {
        subsystems.register(SuperStructure.getInstance());
    }
}
