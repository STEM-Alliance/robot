package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class ScoreHatch extends Command
{
    private final Intake intake = Intake.getInstance();

    public ScoreHatch()
    {
        requires(intake);
        setTimeout(.45);
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
        return isTimedOut();
    }
}
