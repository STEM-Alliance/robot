package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class ClimbTogglePneumatic extends Command
{
    private final Intake intake = Intake.getInstance();

    public ClimbTogglePneumatic()
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