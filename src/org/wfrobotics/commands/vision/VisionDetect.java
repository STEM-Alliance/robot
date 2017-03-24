package org.wfrobotics.commands.vision;

import org.wfrobotics.Utilities;
import org.wfrobotics.vision.NetworkTableCamera;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionDetect extends Command 
{
    public enum MODE {GETDATA, OFF};

    private final NetworkTableCamera camera;
    private final MODE mode;
    
    //boolean endEarly = false;

    public VisionDetect(NetworkTableCamera camera, MODE mode)
    {
        requires(camera);

        this.camera = camera;
        this.mode = mode;
        camera.enable();
    }

    @Override
    protected void initialize()
    {
        //endEarly = false;
        if (mode == MODE.GETDATA)
        {
            camera.enable();
        }
    }

    @Override
    protected void execute()
    {
        Utilities.PrintCommand(camera.getName(), this, mode.toString());

        if (mode == MODE.GETDATA)
        {
            camera.run();
        }
    }

    @Override
    protected boolean isFinished()
    {
        return false;

        //return endEarly;
    }

    @Override
    protected void end()
    {
        camera.disable();
    }

    @Override
    protected void interrupted()
    {
        end();
    }

    public double getDistanceFromCenter()
    {
        return camera.DistanceFromCenter;
    }

    public boolean getIsFound()
    {
        SmartDashboard.putBoolean("getIsFound", camera.InView);
        
        return camera.InView;
    }

    public double getFullWidth()
    {
        return camera.FullWidth;
    }

    public boolean getIsEnabled()
    {
        return camera.isEnabled();
    }

//    public void endEarly()
//    {
//        endEarly = true;
//    }
}
