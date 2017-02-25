package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearDropOff extends CommandGroup 
{    
    private GearDetection camera;
    private AutoDrive drive;
    private PIDController pid;
    
    public VisionGearDropOff() 
    {
        pid = new PIDController(1.6, 0.0015, 0.0001, .5);
        camera = new GearDetection(GearDetection.MODE.GETDATA);
        drive = new AutoDrive(0, 0, 0, 0, 999);
        
        addParallel(camera);
        addSequential(drive); // TODO Create a new constructor for updating, rather than one that does nothing with a big timeout
    }
    
    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double valueY = 0;
        double valueX = 0;
        
        if(camera.getIsFound())
        {
            valueY = pid.update(distanceFromCenter);
        }
        
        if(Math.abs(distanceFromCenter)<.15)
        {
            valueX = -.4;
        }
        
        drive.set(valueX, valueY, 0, -1);  // TODO update AutoDrive after we add that ability to autodrive
    }
}
