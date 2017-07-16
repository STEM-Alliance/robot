package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class Brake extends Command
{
    private boolean request;
    
    public Brake(boolean enable) 
    {
        requires(Robot.driveSubsystem);
        this.request = enable;
    }

    protected void initialize() 
    {
        Robot.driveSubsystem.setBrake(request);
    }

    protected boolean isFinished() 
    {
        return true;
    }
}
