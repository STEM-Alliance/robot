package org.wfrobotics.robot.commands.intake.hatch;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class DownPrecentVoltage extends Command
{
    Intake intake;

    public DownPrecentVoltage()
    {
        requires(Intake.getInstance());
        intake = Intake.getInstance();
    }
    protected void initialize()
    {
        intake.setSpeed(0.3);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
