package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class up extends Command
{
    IntakeSubsystem intake = IntakeSubsystem.getInstance();

    public up()
    {
        requires(IntakeSubsystem.getInstance());
    }

    protected void initialize()
    {
        intake.setSpeed(-0.4);
    }
    protected void execute()
    {


    }

    protected boolean isFinished()
    {
        return !intake.isAtTop();
    }

}
