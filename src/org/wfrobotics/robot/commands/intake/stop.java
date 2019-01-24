package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class stop extends Command
{
    IntakeSubsystem intake = IntakeSubsystem.getInstance();

    public stop()
    {
        requires(IntakeSubsystem.getInstance());
    }
    protected void initialize()
    {
        intake.setSpeed(0);
    }
    protected boolean isFinished()
    {
        return true;
    }

}
