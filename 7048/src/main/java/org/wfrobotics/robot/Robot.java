package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.hardware.Blinkin;
import org.wfrobotics.reuse.hardware.LEDs;
import org.wfrobotics.reuse.hardware.lowleveldriver.BlinkinPatterns.PatternName;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.subsystems.Wrist;

/** @author Team 7048 Red River Rage<p>STEM Alliance of Fargo Moorhead */
public class Robot extends EnhancedRobot
{
    protected Robot() 
    {
        super( RobotConfig.getInstance(),RobotState.getInstance(), IO.getInstance());
	}

    protected void registerRobotSpecific()
    {
        subsystems.register(Elevator.getInstance());
        subsystems.register(Wrist.getInstance());
        subsystems.register(Intake.getInstance());
        subsystems.register(SuperStructure.getInstance());
    }
}
