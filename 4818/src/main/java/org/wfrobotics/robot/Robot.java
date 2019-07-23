package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.subsystems.Vision;

/**
 * Robot: <TBD Robot Name> - 2019
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 * */
public final class Robot extends EnhancedRobot
{
    public Robot()
    {
        super(RobotConfig.getInstance(), RobotState.getInstance(), IO.getInstance());
    }

    protected void registerRobotSpecific()
    {

        subsystems.register(SuperStructure.getInstance());  
        subsystems.register(Vision.getInstance());
    }

    @Override
    public void autonomousInit()
    {
    }
}
