package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class CargoIn extends Command
{
    private final Intake intake = Intake.getInstance();
    private final Wrist wrist = Wrist.getInstance();
    private boolean inCargoMode;

    public CargoIn()
    {
        requires(intake);
    }

    protected void initialize()
    {
        inCargoMode = true;//wrist.inCargoMode();

        if (inCargoMode)
        {
            intake.setCargoSpeed(-1.0);
        }
    }

    protected boolean isFinished()
    {
        return false || !inCargoMode;
    }
}
