package org.wfrobotics.robot.commands.intake.hatch;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class Stop extends Command
{
    Intake intake = Intake.getInstance();

    public Stop()
    {
        requires(Intake.getInstance());
    }
    protected void initialize()
    {
        intake.setSpeed(0);
    }
    protected boolean isFinished()
    {
        return true;
    }

}
