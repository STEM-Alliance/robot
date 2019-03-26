package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.subsystems.Vision;
import org.wfrobotics.robot.subsystems.Climb;
import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Link;

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
        EnhancedRobot.leds = SuperStructure.getInstance().getJeff();
       subsystems.register(Climb.getInstance());
        subsystems.register(Elevator.getInstance());
        subsystems.register(Link.getInstance());
        subsystems.register(Intake.getInstance());
        subsystems.register(SuperStructure.getInstance());  
        subsystems.register(Vision.getInstance());
    }

    @Override
    public void autonomousInit()
    {
        Intake.getInstance().resetAutoModeHatch();
    }
}
