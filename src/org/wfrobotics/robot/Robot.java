package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.VisionProcessor;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.ParellelLink;

/**
 * Robot: Victor - 2018
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 * */
public final class Robot extends EnhancedRobot
{
    //    public static LEDs leds = new Blinkin(9, PatternName.Yellow);

    public final CameraServer visionServer = CameraServer.getInstance();
    VisionProcessor processor = VisionProcessor.getInstance();

    protected void registerRobotSpecific()
    {
        //        RobotState.getInstance().resetVisionState();
        visionServer.register(processor);

        subsystems.register(ParellelLink.getInstance());
        subsystems.register(Intake.getInstance());
        //        subsystems.register(Lift.getInstance());

        backgroundUpdater.register(processor);
    }
}
