package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.VisionProcessor;
import org.wfrobotics.robot.config.ProtoRobotConfig;
import org.wfrobotics.robot.config.ProtoIO;
import org.wfrobotics.robot.subsystems.ExampleSubsystem;

public final class Robot extends EnhancedRobot
{
    public final CameraServer visionServer = CameraServer.getInstance();
    VisionProcessor processor = VisionProcessor.getInstance();

    public static ExampleSubsystem prototypeSubsystem = new ExampleSubsystem();

    public Robot() 
    {
        super(RobotState.getInstance(),
              ProtoRobotConfig.getInstance(),
              new ProtoIO());
    }

    protected void registerRobotSpecific()
    {
        //subsystems.register(SuperStructure.getInstance());
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
