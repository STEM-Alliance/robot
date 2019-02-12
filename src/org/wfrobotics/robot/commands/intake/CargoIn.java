package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class CargoIn extends Command
{
    private final Intake intake = Intake.getInstance();

    public CargoIn()
    {
        requires(intake);
    }

    protected void initialize()
    {
        intake.setCargoSpeed(-1);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
