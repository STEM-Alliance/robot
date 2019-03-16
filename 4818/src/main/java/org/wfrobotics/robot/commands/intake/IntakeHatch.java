package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeHatch extends Command
{
    private final Intake intake = Intake.getInstance();

    public IntakeHatch()
    {
        requires(intake);
    }

    protected void initialize()
    {
        intake.setGrabber(true);
    }

    protected void end()
    {
        intake.setGrabber(false);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
