package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeNone extends Command
{
    private final Intake intake = Intake.getInstance();

    public IntakeNone()
    {
        requires(intake);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
