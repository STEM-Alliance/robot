package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.WinchSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class Winch extends Command
{
    private final WinchSubsystem winch;
    private final IO io;

    public Winch()
    {
        winch = WinchSubsystem.getInstance();
        io = Robot.controls;
        requires(winch);
    }

    protected void execute()
    {
        winch.winch(io.getWinchPercent());
    }

    protected boolean isFinished()
    {
        return false;
    }
}
