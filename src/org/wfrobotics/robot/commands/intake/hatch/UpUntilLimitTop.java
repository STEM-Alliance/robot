package org.wfrobotics.robot.commands.intake.hatch;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class UpUntilLimitTop extends Command
{
    Intake intake = Intake.getInstance();

    public UpUntilLimitTop()
    {
        requires(Intake.getInstance());
    }

    protected void initialize()
    {
        intake.setSpeed(-0.4);
    }
    protected void execute()
    {


    }

    protected boolean isFinished()
    {
        return !intake.isHatchAtTop();
    }

}
