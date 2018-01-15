package org.wfrobotics.prototype;

import org.wfrobotics.prototype.config.IO;
import org.wfrobotics.prototype.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends SampleRobot
{
    public static IntakeSubsystem intakeSubsystem;
    public static IO controls;

    Command autonomousCommand = null;

    public void robotInit()
    {
        
        intakeSubsystem = new IntakeSubsystem();
        
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
