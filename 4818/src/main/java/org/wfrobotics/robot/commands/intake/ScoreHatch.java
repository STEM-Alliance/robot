package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class ScoreHatch extends Command
{
    private final Intake intake = Intake.getInstance();

    public ScoreHatch()
    {
        requires(intake);
        setTimeout(5);
    }

    protected void initialize()
    {
        intake.setGrabber(false);
    }

    protected void end()
    {
        // intake.setGrabber(true);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
