package org.wfrobotics.robot;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.ExampleSubsystem;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

//TODO 2019 switch to non-deprecated RobotBase
@SuppressWarnings("deprecation")
public class Robot extends SampleRobot
{
    public static ExampleSubsystem prototypeSubsystem;
    public static IO controls;

    Command autonomousCommand = null;

    public void robotInit()
    {
        prototypeSubsystem = new ExampleSubsystem();

        controls = new IO();
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();

        while (isOperatorControl() && isEnabled())
        {
            Scheduler.getInstance().run();
        }
    }

    public void autonomous()
    {
        if (autonomousCommand != null) autonomousCommand.start();

        while (isAutonomous() && isEnabled())
        {
            Scheduler.getInstance().run();
        }
    }

    public void disabled()
    {
        while (isDisabled())
        {
            Scheduler.getInstance().run();
        }
    }

    public void test()
    {
        while (isTest() && isEnabled()) { }
    }
}
