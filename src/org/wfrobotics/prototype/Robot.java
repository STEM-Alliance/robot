package org.wfrobotics.prototype;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.prototype.subsystems.ExampleSubsystem;
import org.wfrobotics.robot.subsystems.MecanumSubsystem;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends SampleRobot
{
    public static ExampleSubsystem prototypeSubsystem;
    public static MecanumSubsystem mecanumSubsystem;
    public static IO controls;

    Command autonomousCommand = null;

    public void robotInit()
    {
        mecanumSubsystem = new MecanumSubsystem();
        prototypeSubsystem = new ExampleSubsystem();

        controls = IO.getInstance();
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
