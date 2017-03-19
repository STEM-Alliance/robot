package org.wfrobotics.subsystems;

import org.wfrobotics.Utilities;
import org.wfrobotics.commands.DetectGear;
import org.wfrobotics.vision.NetworkTableCamera;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.networktables.*;


/**
 * Provides information used to shoot the ball.
 * This subsystem translates pictures into data that commands can use to correct how they are aiming.
 *
 */
public class CameraGear extends NetworkTableCamera 
{    
    public final int DESIRED_TARGETS = 2;  // Reflective detectable targets

    public int TargetCount = 0;
    public double DistanceFromCenter = 0;
    public double FullWidth = 0;
    public boolean InView = false;

    public CameraGear()
    {
        super("Target", 0);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new DetectGear(DetectGear.MODE.OFF));
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
        
        //TODO?
//        if(data.size() > LOOKING_FOR_COUNT)
//        {
//            data.sort(Camera.AreaCompare);
//            
//        }
    }
}