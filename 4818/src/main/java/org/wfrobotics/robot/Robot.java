package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.robot.subsystems.Climb;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.reuse.hardware.Canifier;
import org.wfrobotics.reuse.hardware.Canifier.RGB;
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
        super(RobotState.getInstance(), RobotConfig.getInstance(), IO.getInstance());
    }

    protected void registerRobotSpecific()
    {
        EnhancedRobot.leds = new Canifier(6, new RGB(255, 255, 0));

//        subsystems.register(Climb.getInstance());
        subsystems.register(Elevator.getInstance());
        subsystems.register(Link.getInstance());
        subsystems.register(Intake.getInstance());
        subsystems.register(SuperStructure.getInstance());
        DashboardView.startPerformanceCamera();
    }
}
