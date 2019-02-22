package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.hardware.Blinkin;
import org.wfrobotics.reuse.hardware.LEDs;
import org.wfrobotics.reuse.hardware.lowleveldriver.BlinkinPatterns.PatternName;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.VisionProcessor;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.MatchState2018;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Lift;
import org.wfrobotics.robot.subsystems.Winch;
import org.wfrobotics.robot.subsystems.Wrist;

/**
 * Robot: Victor - 2018
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 * */
public final class Robot extends EnhancedRobot
{
    public static LEDs leds = new Blinkin(9, PatternName.Yellow);
    public final CameraServer visionServer = CameraServer.getInstance();
    VisionProcessor processor = VisionProcessor.getInstance();

    public Robot ()
    {
        super(RobotState.getInstance(), RobotConfig.getInstance(), IO.getInstance());
    }

    protected void registerRobotSpecific()
    {
        visionServer.register(processor);

        RobotState.getInstance().resetVisionState();
        backgroundUpdater.register(processor);

        subsystems.register(Intake.getInstance());
        subsystems.register(Lift.getInstance());
        subsystems.register(Winch.getInstance());
        subsystems.register(Wrist.getInstance());

    }

    @Override
    public void disabledPeriodic()
    {
        MatchState2018.getInstance().update();  // Need game-specific data
        super.disabledPeriodic();
    }
}
