package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeUpUntilLimitTop extends Command
{
    Intake intake = Intake.getInstance();

    public IntakeUpUntilLimitTop()
    {
        requires(Intake.getInstance());
    }

    protected void initialize()
    {
        intake.setHatchSpeed(-0.4);
    }

    protected void execute()
    {

    }

    protected boolean isFinished()
    {
        return !intake.hasHatch();
    }

}
