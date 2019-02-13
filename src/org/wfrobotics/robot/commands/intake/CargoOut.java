package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class CargoOut extends Command
{

    Intake intake = Intake.getInstance();
    double timeout;
    public CargoOut(double timeout)
    {
        requires(intake);
        this.timeout = timeout;
    }

    protected void initialize()
    {
        intake.setCargoSpeed(0.6);
        super.initialize();
        setTimeout(timeout);
    }

    protected void execute()
    {

        super.execute();



    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
