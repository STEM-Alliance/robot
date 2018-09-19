package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Winch;

import edu.wpi.first.wpilibj.command.Command;

public class WinchOpenLoop extends Command
{
    private final Winch winch;
    private final IO io;
    private final double speed;

    public WinchOpenLoop()
    {
        winch = Winch.getInstance();
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
