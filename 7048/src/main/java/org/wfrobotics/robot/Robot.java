package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;

/**
 * Robot: 
 * @author Team 7048<p>STEM Alliance of Fargo Moorhead
 * */
public final class Robot extends EnhancedRobot
{
    //    public static LEDs leds = new Blinkin(9, PatternName.Yellow);

    public final CameraServer visionServer = CameraServer.getInstance();
    //    VisionProcessor processor = VisionProcessor.getInstance();

    public Robot()
    {
        super(RobotState.getInstance(),
              RobotConfig.getInstance(),
              IO.getInstance());
    }
    
    protected void registerRobotSpecific()
    {
        //        RobotState.getInstance().resetVisionState();
        //        visionServer.register(processor);
        // backgroundUpdater.register(processor);

        // subsystems.register(Climb.getInstance());
        // subsystems.register(Elevator.getInstance());
        // subsystems.register(Link.getInstance());
        // subsystems.register(Intake.getInstance());
        // subsystems.register(Wrist.getInstance());

        //        backgroundUpdater.register(processor);
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
