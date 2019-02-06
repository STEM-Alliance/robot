package org.wfrobotics.robot.commands.intake.hatch;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class JoyStickMove extends Command
{
    Intake intake = Intake.getInstance();

    public JoyStickMove()
    {
        requires(Intake.getInstance());
    }

    protected void execute()
    {
        Intake.getInstance().setSpeed(IO.getInstance().getLinkStick());
    }

    protected boolean isFinished()
    {
        return false;
    }
}
