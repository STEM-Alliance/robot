package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionGearDropOff extends CommandGroup 
{    
    private GearDetection camera;
    private AutoDrive drive;
    
    public VisionGearDropOff() 
    {
        camera = new GearDetection(GearDetection.MODE.GETDATA);
        drive = new AutoDrive(0, 0, 0, 0, 999);
        
        addParallel(camera);
        addSequential(drive); // TODO Create a new constructor for updating, rather than one that does nothing with a big timeout
    }
    
    protected void execute()
    {
        //TargetData data = camera.getData();  // TODO grab the data from the command
        
        drive.set(0, 0, 0, 0);  // TODO update AutoDrive after we add that ability to autodrive
    }
}
