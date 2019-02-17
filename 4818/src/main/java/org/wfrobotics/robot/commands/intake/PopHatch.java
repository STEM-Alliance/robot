package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class PopHatch extends Command
{
    private final Intake intake = Intake.getInstance();
    private final Wrist wrist = Wrist.getInstance();
    private boolean inHatchMode;

    public PopHatch()
    {
        requires(intake);
        setTimeout(.45);
    }

    protected void initialize()
    {
        inHatchMode = true;//wrist.inHatchMode();

        if (inHatchMode)
        {
            intake.setPoppers(true);
        }
    }

    protected void end()
    {
        intake.setPoppers(false);
    }

    protected boolean isFinished()
    {
        return isTimedOut() || !inHatchMode;
    }
}
