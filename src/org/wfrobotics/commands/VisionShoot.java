package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.AutoDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionShoot extends CommandGroup 
{    
    private ShooterDetection camera;
    private AutoDrive drive;
    
    public VisionShoot() 
    {
        camera = new ShooterDetection(ShooterDetection.MODE.GETDATA);
        drive = new AutoDrive(0, 0, 0, 0, 999);
        
        addParallel(camera);
        addSequential(drive); // TODO Create a new constructor for updating, rather than one that does nothing with a big timeout
    }
    
    protected void execute()
    {
        // TODO make this like VisionGear
    }
}

