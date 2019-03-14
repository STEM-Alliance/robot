package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class SetHatch extends Command
{
    private final Intake intake = Intake.getInstance();
    boolean closed = false;

    public SetHatch(boolean closed)
    {
        requires(intake);
        setTimeout(.45);
        this.closed = closed;
    }

    protected void initialize()
    {
        intake.setPoppers(closed);
    }

    protected void end()
    {
        //intake.setPoppers(false);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
