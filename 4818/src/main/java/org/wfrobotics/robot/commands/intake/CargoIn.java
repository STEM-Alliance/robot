package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;

public class CargoIn extends Command
{
    private final Intake intake = Intake.getInstance();

    public CargoIn()
    {
        requires(intake);
        setTimeout(1);
    }

    protected void initialize()
    {
        intake.setCargoSpeed(0.8);
    }
    protected void end()
    {
        intake.setCargoSpeed(0.0);
    }
    protected boolean isFinished()
    {
        return isCanceled() || isTimedOut() || Link.getInstance().getPosition() > 90 || SuperStructure.getInstance().getHasCargo();
        // return isCanceled() || isTimedOut() || SuperStructure.getInstance().getHasCargo();
    }
}
