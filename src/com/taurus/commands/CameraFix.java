package com.taurus.commands;

import com.taurus.robot.Robot;
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
    
    protected void initialize() 
    {
        if(ranOnce == false)
        {
            Vision.getInstance().fixCamera(true);
            startTime = Timer.getFPGATimestamp();
        }
    }
    
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

    protected boolean isFinished() 
    {
        return false;
    }
    
    protected void end() 
    {
    }
    
    protected void interrupted() 
    {
    }
}
