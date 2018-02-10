package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeSolenoidNone extends Command
{
    public IntakeSolenoidNone()
    {
        requires(Robot.intakeSolenoidSubsystem);
    }

    protected void initialize()
    {
        Robot.intakeSolenoidSubsystem.setHorizontal(false);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
