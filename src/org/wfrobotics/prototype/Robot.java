package org.wfrobotics.prototype;

import org.wfrobotics.prototype.commands.AutoDrive;
import org.wfrobotics.prototype.commands.AutoTurnAndTurn;
import org.wfrobotics.prototype.config.IO;
import org.wfrobotics.prototype.subsystems.ExampleSubsystem;
import org.wfrobotics.prototype.subsystems.ExampleSubsystem2;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends SampleRobot
{
    public static ExampleSubsystem2 prototypeSubsystem;
    public static IO controls;
    Command autonomousCommand;


    public void robotInit()
    {
        prototypeSubsystem = new ExampleSubsystem2();
        autonomousCommand = new AutoTurnAndTurn();
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
