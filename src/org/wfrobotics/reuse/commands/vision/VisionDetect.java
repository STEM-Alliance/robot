package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.subsystems.vision.NetworkTableCamera;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionDetect extends Command 
{
    private final NetworkTableCamera camera;

    public VisionDetect(NetworkTableCamera camera)
    {
        requires(camera);

        this.camera = camera;
    }

    protected void execute()
    {
        String state = (camera.isEnabled()) ? "Enabled" : "Disabled";
        
        Utilities.PrintCommand(camera.getName(), this, state);

        camera.update();
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        camera.disable();
    }

    protected void interrupted()
    {
        end();
    }

    public double getDistanceFromCenter()
    {
        return camera.getDistanceFromCenter();
    }

    public boolean getIsFound()
    {
        boolean inView = camera.getInView();
        SmartDashboard.putBoolean("getIsFound", inView);
        
        return inView;
    }

    public double getFullWidth()
    {
        return camera.getFullWidth();
    }

    public boolean getIsEnabled()
    {
        return camera.isEnabled();
    }
}
