package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.reuse.subsystems.vision.NetworkTableCamera;

import edu.wpi.first.wpilibj.command.Command;

public class VisionEnable extends Command
{
    private final NetworkTableCamera camera;
    
    public VisionEnable(NetworkTableCamera camera)
    {
        requires(camera);
        
        this.camera = camera;
    }

    protected void initialize()
    {
        camera.enable();
    }
    
    protected boolean isFinished()
    {
        return true;
    }
}
