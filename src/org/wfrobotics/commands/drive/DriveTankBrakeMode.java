package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTankBrakeMode extends Command
{
    private boolean enable;
    
    public DriveTankBrakeMode(boolean enable) 
    {
        requires(Robot.driveSubsystem);
        this.enable = enable;
    }

    protected void initialize() 
    {
        Robot.driveSubsystem.setBrake(enable);
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
