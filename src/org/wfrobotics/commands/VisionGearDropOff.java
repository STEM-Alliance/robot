package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionGearDropOff extends CommandGroup 
{    
    private GearCamera camera;
    private AutoDrive drive;
    
    public VisionGearDropOff() 
    {
        camera = new GearCamera(GearCamera.MODE.GETDATA);
        drive = new AutoDrive(0, 0, 0, 0, 999);
        
        addParallel(camera);
        addSequential(drive); // TODO Create a new constructor for updating, rather than one that does nothing with a big timeout
    }
    
    protected void execute()
    {
        TargetData data = camera.getData();
        
        drive.set(0, 0, 0, 0);  // TODO update AutoDrive after we add that ability to autodrive
    }
}
