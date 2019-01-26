package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class down extends Command
{
    IntakeSubsystem intake;

    public down()
    {
        requires(IntakeSubsystem.getInstance());
        intake = IntakeSubsystem.getInstance();
    }
    protected void initialize()
    {
        intake.setSpeed(0.3);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
