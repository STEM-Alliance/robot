package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeHatchManual extends Command
{
    private final Intake intake = Intake.getInstance();

    private boolean setValue;

    public IntakeHatchManual(boolean value)
    {
        requires(intake);
        setValue = value;
    }

    protected void initialize()
    {
        intake.setGrabber(setValue);
    }

    protected void end()
    {
        intake.setGrabber(!setValue);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
