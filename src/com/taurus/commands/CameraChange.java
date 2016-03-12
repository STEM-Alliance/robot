package com.taurus.commands;

import com.taurus.robot.Robot;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class CameraChange extends Command {

    boolean done = false;
    boolean back = false;
    private double startTime;
    
    public CameraChange(boolean back)
    {
        requires(Robot.backCameraSubsystem);
        this.back = back;
    }
    
    protected void initialize() 
    {
        done = false;
        Vision.getInstance().fixCamera(true);
        startTime = Timer.getFPGATimestamp();
    }
    
    protected void execute()
    {
        Robot.backCameraSubsystem.setCameraBack(back);
        
        if(!done)
        {
            if(Timer.getFPGATimestamp() - startTime > 1)
            {
                Vision.getInstance().fixCamera(false);
                done = true;
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
