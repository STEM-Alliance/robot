package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeUpUntilLimitTop extends Command
{
    private final Intake intake = Intake.getInstance();

    public IntakeUpUntilLimitTop()
    {
        requires(intake);
    }

    protected void initialize()
    {
        intake.setHatchSpeed(-0.4);
    }

    protected boolean isFinished()
    {
        return !intake.hasHatch();
    }

    protected void end()
    {
        intake.setHatchSpeed(0.0);
    }
}
