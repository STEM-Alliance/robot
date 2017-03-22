package org.wfrobotics.subsystems;

import org.wfrobotics.Utilities;
import org.wfrobotics.commands.vision.VisionDetect;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.vision.NetworkTableCamera;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Provides information used to shoot the ball.
 * This subsystem translates pictures into data that commands can use to correct how they are aiming.
 *
 */
public class CameraGear extends NetworkTableCamera 
{
    public CameraGear()
    {
        super("Target", 0, 2);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new VisionDetect(Robot.targetGearSubsystem, VisionDetect.MODE.OFF));
    }
    
    public void run()
    {
        double xAverage = 0;
        double xPercent = 0;
        
        getUpdatedData();
        
        TargetCount = data.size();
        SmartDashboard.putNumber("GearTargets", TargetCount);
        
        if(TargetCount == DESIRED_TARGETS)
        {
            xAverage = (data.get(0).x + data.get(1).x)/2.0;
            xPercent = xAverage / table.imageWidth;
            DistanceFromCenter = Utilities.scaleToRange(xPercent, 0, 1, -1, 1);
            InView = true;
            
            if(data.get(1).x > data.get(0).x)
            {
                FullWidth = (data.get(1).x + data.get(1).width/2) - (data.get(0).x - data.get(0).width/2);
            }
            else
            {
                FullWidth = (data.get(0).x + data.get(0).width/2) - (data.get(1).x - data.get(1).width/2);
            }
        }
        else if (TargetCount == 1)
        {
            //SmartDashboard.putNumber("percent", data.get(0).x / table.imageWidth);
            DistanceFromCenter = Utilities.scaleToRange(data.get(0).x / table.imageWidth, 0.0, 1.0, -1.0, 1.0);
            FullWidth = data.get(0).width;
            InView = false;
        }
        else
        {
            DistanceFromCenter = 0;
            FullWidth = 0;
            InView = false;
        }
    }
}