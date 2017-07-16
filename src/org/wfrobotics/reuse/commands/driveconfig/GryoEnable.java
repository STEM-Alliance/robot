package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class GryoEnable extends Command
{
    private boolean request;
    
    public GryoEnable(boolean enable)
    {
        requires(Robot.driveSubsystem);
        request = enable;
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.configSwerve.gyroEnable = request;
    }

    protected boolean isFinished()
    {
        return true;
    }
}
