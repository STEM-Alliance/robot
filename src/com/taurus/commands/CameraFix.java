package com.taurus.commands;

import com.taurus.robot.Robot;
import com.taurus.subsystems.CameraSubsystem;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class CameraFix extends Command {

    static boolean ranOnce = false;
    
    double startTime = 0;
    
    public CameraFix()
    {
        requires(Robot.cameraSubsystem);
    }
    
 // Called just before this Command runs the first time
    protected void initialize() 
    {
        if(ranOnce == false)
        {
            Vision.getInstance().fixCamera(true);
            startTime = Timer.getFPGATimestamp();
        }
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
        if(ranOnce == false)
        {
            if(Timer.getFPGATimestamp() - startTime > 2)
            {
                Vision.getInstance().fixCamera(false);
                ranOnce = true;
            }
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() 
    {
        return false;
    }
    
    // Called once after isFinished returns true
    protected void end() 
    {
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() 
    {
        
    }
}
