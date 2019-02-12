package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeDownPrecentVoltage extends Command
{
    private final Intake intake = Intake.getInstance();

    public IntakeDownPrecentVoltage()
    {
        requires(intake);
    }

    protected void initialize()
    {
        intake.setHatchSpeed(0.3);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
