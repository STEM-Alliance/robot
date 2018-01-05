package org.wfrobotics.prototype;

import org.wfrobotics.prototype.config.IO;
import org.wfrobotics.prototype.subsystems.ArmSubsystem;
import org.wfrobotics.prototype.subsystems.HandSubsystem;
import org.wfrobotics.prototype.subsystems.ShiftingSubsystem;
import org.wfrobotics.prototype.subsystems.TankSubsystem;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends SampleRobot
{
    public static ArmSubsystem armSubsystem;
    public static TankSubsystem tankSubsystem;
    public static HandSubsystem handSubsystem;
    public static ShiftingSubsystem shiftingSubsystem;

    public static IO controls;

    Command autonomousCommand = null;

    public void robotInit()
    {
        armSubsystem = new ArmSubsystem();
        tankSubsystem = new TankSubsystem();
        handSubsystem = new HandSubsystem();
        shiftingSubsystem = new ShiftingSubsystem();
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
