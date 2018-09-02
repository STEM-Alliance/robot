package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.WinchSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class Winch extends Command
{
    private final WinchSubsystem winch;
    private final IO io;
    private final double speed;

    public Winch()
    {
        winch = WinchSubsystem.getInstance();
        io = Robot.controls;
        requires(winch);
        speed = RobotConfig.getInstance().kWinchSpeed;
    }

    protected void execute()
    {
        winch.winch(io.getWinchPercent() * speed);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
