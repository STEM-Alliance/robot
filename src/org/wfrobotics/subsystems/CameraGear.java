package org.wfrobotics.subsystems;

import org.wfrobotics.Utilities;
import org.wfrobotics.commands.GearDetection;
import org.wfrobotics.commands.drive.DriveSwerve;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * Provides information used to shoot the ball.
 * This subsystem translates pictures into data that commands can use to correct how they are aiming.
 *
 */
public class CameraGear extends Camera 
{
    private final int LOOKING_FOR_COUNT = 2;
    
    public double DistanceFromCenter = 0;
    public boolean InView = false;

    public CameraGear()
    {
        super("Gear");
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new GearDetection(GearDetection.MODE.OFF));
    }
    
    public void run()
    {
        getUpdatedData();
        
        double xAverage = 0;
        double xPercent = 0;
        
        if(data.size() == LOOKING_FOR_COUNT)
        {
            xAverage = (data.get(0).x + data.get(1).x)/2.0;
            xPercent = xAverage / table.imageWidth;
            DistanceFromCenter = Utilities.scaleToRange(xPercent, 0, 1, -1, 1);
            InView = true;
        }
        else
        {
            DistanceFromCenter = 0;
            InView = false;
        }

        SmartDashboard.putNumber("GearDistanceX", DistanceFromCenter);
        
        //TODO?
//        if(data.size() > LOOKING_FOR_COUNT)
//        {
//            data.sort(Camera.AreaCompare);
//            
//        }
    }
}