package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Climb;
import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.subsystems.Wrist;

/**
 * Robot: 2019
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 * */
public final class Robot extends EnhancedRobot
{
    //    public final CameraServer visionServer = CameraServer.getInstance();
    //    public final VisionProcessor processor = VisionProcessor.getInstance();

    public Robot()
    {
        super(RobotState.getInstance(), RobotConfig.getInstance(), IO.getInstance());
    }

    protected void registerRobotSpecific()
    {
        //        leds = new Blinkin(9, PatternName.Yellow);
        //        RobotState.getInstance().resetVisionState();
        //        visionServer.register(processor);

        subsystems.register(Climb.getInstance());
        subsystems.register(Elevator.getInstance());
        subsystems.register(Link.getInstance());
        subsystems.register(Intake.getInstance());
        subsystems.register(SuperStructure.getInstance());
        subsystems.register(Wrist.getInstance());

        //        backgroundUpdater.register(processor);
    }
}
