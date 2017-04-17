package org.wfrobotics.robot.subsystems;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.commands.vision.VisionDetect;
import org.wfrobotics.reuse.subsystems.NetworkTableCamera;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Provides information used to shoot the ball.
 * This subsystem translates pictures into data that commands can use to correct how they are aiming.
 */
public class CameraShooter extends NetworkTableCamera 
{
    public CameraShooter()
    {
        super("Target", 1, 2);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new VisionDetect(Robot.targetShooterSubsystem, VisionDetect.MODE.OFF));
    }    

    public void run()
    {
        double xAverage = 0;
        double xPercent = 0;
        
        getUpdatedData();
        
        SmartDashboard.putNumber("ShooterTargets", data.size());
        
        if(data.size() == DESIRED_TARGETS)
        {
            xAverage = (data.get(0).x + data.get(1).x)/2.0;
            xPercent = xAverage / table.imageWidth;
            DistanceFromCenter = Utilities.scaleToRange(xPercent, 0, 1, -1, 1);
            FullWidth = Math.max(data.get(0).width, data.get(1).width);
            InView = true;
            
        }
        else if (data.size() == 1)
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