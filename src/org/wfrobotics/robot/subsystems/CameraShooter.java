package org.wfrobotics.robot.subsystems;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.commands.vision.VisionDetect;
import org.wfrobotics.reuse.subsystems.vision.NetworkTableCamera;
import org.wfrobotics.reuse.subsystems.vision.TargetTableBasic;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This subsystem translates pictures into data that commands can use
 */
public class CameraShooter extends NetworkTableCamera 
{
    public final int DESIRED_TARGETS = 2;  // Reflective things to sense
    //TODO figure out how to get this automatically
    public double imageWidth = 640.0;
    public double imageHeight = 480.0;
    
    protected double DistanceFromCenter = 0;
    protected double FullWidth = 0;
    protected boolean InView = false;
    
    public CameraShooter()
    {
        super(1);
        table = new TargetTableBasic("Target");
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new VisionDetect(Robot.targetShooterSubsystem));
    }    

    public void processData()
    {
        double xAverage = 0;
        double xPercent = 0;
        
        SmartDashboard.putNumber("ShooterTargets", data.size());
        
        if(data.size() == DESIRED_TARGETS)
        {
            xAverage = (data.get(0).x + data.get(1).x)/2.0;
            xPercent = xAverage / imageWidth;
            DistanceFromCenter = Utilities.scaleToRange(xPercent, 0, 1, -1, 1);
            FullWidth = Math.max(data.get(0).width, data.get(1).width);
            InView = true;
            
        }
        else if (data.size() == 1)
        {
            //SmartDashboard.putNumber("percent", data.get(0).x / table.imageWidth);
            DistanceFromCenter = Utilities.scaleToRange(data.get(0).x / imageWidth, 0.0, 1.0, -1.0, 1.0);
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

    protected void notifyEnabled()
    {
        DistanceFromCenter = 0;
        FullWidth = 0;
        InView = false;
    }
    
    public double getDistanceFromCenter()
    {
        double result = 0;
        
        if (isEnabled())
        {
            result = DistanceFromCenter;
        }
        return result;
    }

    public double getFullWidth()
    {
        double result = 0;
        
        if (isEnabled())
        {
            result = FullWidth;
        }
        return result;
    }

    public boolean getInView()
    {
        boolean result = false;
        
        if (isEnabled())
        {
            result = InView;
        }
        return result;
    }
}