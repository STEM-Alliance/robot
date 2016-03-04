package com.taurus.commands;

import com.taurus.robot.Robot;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class CameraChange extends Command {

    boolean done = false;
    boolean back = false;
    
    public CameraChange(boolean back)
    {
        requires(Robot.backCameraSubsystem);
        this.back = back;
    }
    
    protected void initialize() 
    {
        done = false;
    }
    
    protected void execute()
    {
        Robot.backCameraSubsystem.setCameraBack(back);
        done = true;
    }

    protected boolean isFinished() 
    {
        return done;
    }
    
    protected void end() 
    {
    }
    
    protected void interrupted() 
    {
    }
}
