package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Winch;

import edu.wpi.first.wpilibj.command.Command;

public final class WinchOpenLoop extends Command
{
    private final Winch winch = Winch.getInstance();
    private final IO io = IO.getInstance();
    private final double speed;

    public WinchOpenLoop()
    {
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
