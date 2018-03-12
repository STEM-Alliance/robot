package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

/** Follow path profile (Motion Profile Mode)*/
public class DrivePath extends Command
{
    public DrivePath()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void initialize()
    {
        Robot.prototypeSubsystem.drivePath();
    }

    protected boolean isFinished()
    {
        return Robot.prototypeSubsystem.onTarget();
    }
}