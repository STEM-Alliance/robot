package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class WristManuel extends Command
{
    public WristManuel()
    {
        requires(Robot.wrist);
    }

    protected void execute()
    {
        Robot.wrist.setSpeed(Robot.controls.getWrist());
    }

    protected boolean isFinished()
    {
        return true;
    }
}
