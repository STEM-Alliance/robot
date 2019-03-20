package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;

public class CargoIn extends Command
{
    private final Intake intake = Intake.getInstance();

    public CargoIn()
    {
        requires(intake);
        setTimeout(1.0);
    }

    protected void initialize()
    {
        intake.setCargoSpeed(1.0);
    }

    protected void end()
    {
        intake.setCargoSpeed(0.0);
    }
    
    protected boolean isFinished()
    {
        return isTimedOut() ||  SuperStructure.getInstance().getHasCargo();
    }
}
