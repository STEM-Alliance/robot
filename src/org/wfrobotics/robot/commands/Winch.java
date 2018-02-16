package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class Winch extends Command
{
    public Winch()
    {
        requires(Robot.winch);
    }

    protected void execute()
    {
        // TODO Winch
    }

    protected boolean isFinished()
    {
        return false;
    }
}
