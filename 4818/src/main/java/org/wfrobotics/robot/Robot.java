package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.VisionProcessor;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.reuse.hardware.Blinkin;
import org.wfrobotics.reuse.hardware.lowleveldriver.BlinkinPatterns.PatternName;

/**
 * Robot: Victor - 2018
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 * */
public final class Robot extends EnhancedRobot
{
    public final CameraServer visionServer = CameraServer.getInstance();
    VisionProcessor processor = VisionProcessor.getInstance();

    public Robot()
    {
        super(RobotState.getInstance(),
              RobotConfig.getInstance(),
              IO.getInstance());
    }

    protected void registerRobotSpecific()
    {
        leds = new Blinkin(9, PatternName.Yellow);
        subsystems.register(SuperStructure.getInstance());
        visionServer.register(processor);
        backgroundUpdater.register(processor);
        RobotState.getInstance().resetVisionState();

    }

    @Override
    public void disabledPeriodic()
    {

        super.disabledPeriodic();
    }
    @Override
    public void teleopPeriodic()
    {
        subsystems.update();
    }
}
