package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.commands.vision.VisionTracking;
import org.wfrobotics.reuse.subsystems.NetworkTableCamera;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class CameraTrack extends NetworkTableCamera {

    
    public enum State { searching, found, locked, off };
        
    public State currentState;
       
    double lastX = 0.0, 
           lastY = 0.0;
    
    boolean hasTarget = false;
    
    public CameraTrack()
    {
        super("Target", 0, 1);
        
        currentState = State.searching;
        this.enable();
    }
    
    @Override
    protected void initDefaultCommand()
    {
        //setDefaultCommand(new VisionDetect(Robot.camServoSubsystem, MODE.GETDATA));
        setDefaultCommand(new VisionTracking(Robot.camTrackSubsystem));
    }

    @Override
    public void run()
    {       
        getUpdatedData();
        SmartDashboard.putNumber("ServoTargets", TargetCount);
        SmartDashboard.putBoolean("hasTarget", hasTarget);
        
        if (TargetCount == 0)
        {
            lookingForTarget();
            hasTarget = false;

            if (currentState != State.searching)
            {
                currentState = State.searching;
            }
        }
        if ( TargetCount >= 1)
        {
            hasTarget = true;
            currentState = State.found;   
        }
                
    }

    /**
     * 
     * @return x offset from 0 to 1
     */
    public double getXoffset()
    {
        TargetData validData = data.get(0);     

        double xoffset = ((validData.x - (table.imageWidth / 2)) / (table.imageWidth / 2));
        
        return xoffset;
    }
    public double getYoffset()
    {
        TargetData validData = data.get(0);     
 
        double yoffset = ((validData.y - (table.imageHeight / 2)) / (table.imageHeight / 2));
                
        return yoffset;
    }
   
    public void lookingForTarget()
    {
//        do{
//            
//                if (lastX <= 1)
//                {
//                    //servoX.set(lastX);
//                    lastX += 0.1;
//                }
//                else
//                {
//                    if (lastY <= 1)
//                    {
//                        lastY = 0;
//                    }
//                   lastY = lastY + 0.1;
//                   lastX = 0;
//                   //servoY.set(lastY);
//            }
//        }while (currentState == State.searching);
    }
   
    public boolean getHasTarget()
    {
        return hasTarget;
    }
}

