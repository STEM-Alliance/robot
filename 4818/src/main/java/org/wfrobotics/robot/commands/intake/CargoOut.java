package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class CargoOut extends Command
{
    private final Intake intake = Intake.getInstance();
    private final Wrist wrist = Wrist.getInstance();
    private boolean inCargoMode;

    public CargoOut(double timeout)
    {
        requires(intake);
        setTimeout(timeout);
    }

    protected void initialize()
    {
        inCargoMode = wrist.inCargoMode();

        if (inCargoMode)
        {
            intake.setCargoSpeed(0.6);
        }
    }

    protected boolean isFinished()
    {
        return isTimedOut() || !inCargoMode;
    }
}
