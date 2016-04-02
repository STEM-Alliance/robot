package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveBrakeMode extends Command
{
    private boolean enable;
    
    public DriveBrakeMode(boolean enable) 
    {
        requires(Robot.tankDriveSubsystem);
        this.enable = enable;
    }

    protected void initialize() 
    {
        Robot.tankDriveSubsystem.setBrakeMode(enable);
    }

    protected void execute() 
    {
        
    }

    protected boolean isFinished() 
    {
        return true;
    }

    protected void end()
    {
        
    }

    protected void interrupted() 
    {
        
    }
}
