package org.wfrobotics.prototype;

import org.wfrobotics.prototype.config.IO;
import org.wfrobotics.prototype.subsystems.IntakeSolenoidSubsystem;
import org.wfrobotics.prototype.subsystems.IntakeSubsystem;
import org.wfrobotics.prototype.subsystems.LiftSubsystem;
import org.wfrobotics.prototype.subsystems.ShiftingSubsystem;
import org.wfrobotics.prototype.subsystems.TankSubsystem;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends SampleRobot
{
    public static TankSubsystem tankSubsystem;
    public static ShiftingSubsystem shiftingSubsystem;
    public static LiftSubsystem liftSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static IntakeSolenoidSubsystem intakeSolenoidSubsystem;

    public static IO controls;

    Command autonomousCommand = null;

    public void robotInit()
    {
        liftSubsystem = new LiftSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        tankSubsystem = new TankSubsystem();
        shiftingSubsystem = new ShiftingSubsystem();
        intakeSolenoidSubsystem = new IntakeSolenoidSubsystem();
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
