package org.wfrobotics.robot;

import org.wfrobotics.reuse.PrototypeEnhancedRobot;
import org.wfrobotics.robot.config.ProtoRobotConfig;
import org.wfrobotics.robot.config.ProtoIO;
import org.wfrobotics.robot.subsystems.ExampleSubsystem;

public final class Robot extends PrototypeEnhancedRobot
{
    public static ExampleSubsystem exampleSubsystem = new ExampleSubsystem();

    public Robot() 
    {
        super(ProtoRobotConfig.getInstance(),RobotState.getInstance(), new ProtoIO());
    }

    protected void registerRobotSpecific()
    {
        subsystems.register(exampleSubsystem);
    }
}
