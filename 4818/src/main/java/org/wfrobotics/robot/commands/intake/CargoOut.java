package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class CargoOut extends Command
{
    private final Intake intake = Intake.getInstance();

    public CargoOut(double timeout)
    {
        requires(intake);
        setTimeout(timeout);
    }

    protected void initialize()
    {
        intake.setCargoSpeed(-1.0);
    }
    protected void end()
    {
        intake.setCargoSpeed(0.0);
    }
    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
