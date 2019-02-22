package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class PopHatch extends Command
{
    private final Intake intake = Intake.getInstance();

    public PopHatch()
    {
        requires(intake);
        setTimeout(.45);
    }

    protected void initialize()
    {
        intake.setPoppers(true);
    }

    protected void end()
    {
        intake.setPoppers(false);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
