package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.reuse.subsystems.NetworkTableCamera;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionTracking extends CommandGroup {

    public final VisionDetect camera;
    public final TrackTarget track;
    
    public VisionTracking(NetworkTableCamera camera)
    {
        this.camera = new VisionDetect(camera, VisionDetect.MODE.GETDATA);
        track = new TrackTarget();
        
        addParallel(this.camera);
        addSequential(track);
    }
    @Override
    protected void execute()
    {
        
    }
}
