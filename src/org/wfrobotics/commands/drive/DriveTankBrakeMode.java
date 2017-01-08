package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.TankDriveSubsystem;

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
        ((TankDriveSubsystem)Robot.driveSubsystem).setBrakeMode(enable);
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
