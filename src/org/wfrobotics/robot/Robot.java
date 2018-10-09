package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.hardware.LEDs;
import org.wfrobotics.reuse.hardware.lowleveldriver.RevLEDs.PatternName;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.VisionListener;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageConfig;
import org.wfrobotics.robot.config.MatchState2018;
import org.wfrobotics.robot.paths.TrajectoryGenerator;

/**
 * Robot: Victor - 2018
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 * */
public final class Robot extends EnhancedRobot
{
    public static LEDs leds = new LEDs(9, PatternName.Yellow);
    public final CameraServer visionServer = CameraServer.getInstance();

    protected void registerRobotSpecific()
    {
        visionServer.SetConfig(new VisionMessageConfig(0));
        //        visionServer.SetConfig(new VisionMessageConfig(0,1, new ArrayList<>(Arrays.asList(new Boolean[] {true, true}))));
        TrajectoryGenerator.getInstance().generateTrajectories();

        VisionListener listener = new VisionListener();
        visionServer.AddListener(listener);

        //        VisionProcessor processor = new VisionProcessor();
        //        visionServer.AddListener(processor);

        //        subsystems.register(Intake.getInstance());
        //        subsystems.register(Lift.getInstance());
        //        subsystems.register(Winch.getInstance());
        //        subsystems.register(Wrist.getInstance());
    }

    @Override
    public void disabledPeriodic()
    {
        MatchState2018.getInstance().update();  // Need game-specific data
        super.disabledPeriodic();
    }
}
