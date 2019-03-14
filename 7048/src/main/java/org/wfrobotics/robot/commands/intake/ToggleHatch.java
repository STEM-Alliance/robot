package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class ToggleHatch extends Command
{
    private final Intake intake = Intake.getInstance();

    public ToggleHatch()
    {
        requires(intake);
        setTimeout(.45);
    }

    protected void initialize()
    {
        intake.setPoppers(!intake.getPoppers());
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
