package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.config.EnhancedRobotConfig;
import org.wfrobotics.reuse.hardware.Blinkin;
import org.wfrobotics.reuse.hardware.Canifier;
import org.wfrobotics.reuse.hardware.LEDs;
import org.wfrobotics.reuse.hardware.Canifier.RGB;
import org.wfrobotics.reuse.hardware.lowleveldriver.BlinkinPatterns.PatternName;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.subsystems.Wrist;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.VisionProcessor;

import edu.wpi.first.wpilibj.command.Command;

/** @author Team 7048 Red River Rage<p>STEM Alliance of Fargo Moorhead */
public class Robot extends EnhancedRobot
{
    protected Robot() 
    {
        super(RobotState.getInstance(), RobotConfig.getInstance(), IO.getInstance());
	}

    protected void registerRobotSpecific()
    {
        //subsystems.register(Elevator.getInstance());
        //subsystems.register(Wrist.getInstance());
        //subsystems.register(Intake.getInstance());
        //subsystems.register(SuperStructure.getInstance());
    }
}
