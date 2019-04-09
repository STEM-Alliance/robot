package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.hardware.Blinkin;
import org.wfrobotics.reuse.hardware.LEDs;
import org.wfrobotics.reuse.hardware.lowleveldriver.BlinkinPatterns.PatternName;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.subsystems.Wrist;
import org.wfrobotics.robot.subsystems.WristPneumatic;

/** @author Team 7048 Red River Rage<p>STEM Alliance of Fargo Moorhead */
public class Robot extends EnhancedRobot
{
    protected Robot() 
    {
        super( RobotConfig.getInstance(),RobotState.getInstance(), IO.getInstance());
	}

    protected void registerRobotSpecific()
    {
        subsystems.register(Intake.getInstance());
        subsystems.register(SuperStructure.getInstance());
        subsystems.register(WristPneumatic.getInstance());
        DashboardView.startCustomCamera(320, 240, 20);  // Faster than normal camera stream
    }
}
