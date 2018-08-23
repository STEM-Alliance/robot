package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class SmartOutake extends Command
{
    protected final IntakeSubsystem intake = IntakeSubsystem.getInstance();

    public SmartOutake()
    {
        requires(intake);
        setTimeout(.5);
    }

    protected void execute()
    {
        intake.setIntake(0.30);
        if (timeSinceInitialized() > .2)
        {
            intake.setJaws(true);
        }
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
