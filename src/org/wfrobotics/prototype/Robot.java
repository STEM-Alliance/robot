package org.wfrobotics.prototype;

import org.wfrobotics.prototype.commands.DrivePath;
import org.wfrobotics.prototype.config.IO;
import org.wfrobotics.prototype.config.RobotConfig;
import org.wfrobotics.prototype.subsystems.ExampleSubsystem;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends SampleRobot
{
    public static RobotConfig config = new RobotConfig();
    public static ExampleSubsystem prototypeSubsystem;
    public static IO controls;

    Command autonomousCommand = null;

    public void robotInit()
    {
        prototypeSubsystem = new ExampleSubsystem(config);

        controls = new IO();
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();

        while (isOperatorControl() && isEnabled())
        {
            allPeriodic();
            Scheduler.getInstance().run();
        }
    }

    public void autonomous()
    {
        autonomousCommand = new DrivePath();
        if (autonomousCommand != null) autonomousCommand.start();

        while (isAutonomous() && isEnabled())
        {
            allPeriodic();
            Scheduler.getInstance().run();
        }
    }

    public void disabled()
    {
        while (isDisabled())
        {
            allPeriodic();
            Scheduler.getInstance().run();
        }
    }

    public void test()
    {
        while (isTest() && isEnabled()) { }
    }

    private void allPeriodic()
    {
        prototypeSubsystem.reportState();
    }
}
