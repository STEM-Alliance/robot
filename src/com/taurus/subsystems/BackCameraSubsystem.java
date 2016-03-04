package com.taurus.subsystems;

import com.taurus.commands.CameraChange;
import com.taurus.commands.CameraFix;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.command.Subsystem;

public class BackCameraSubsystem extends Subsystem {

    ////////////////////////////////////////////////////////////
    // THIS SHOULDN'T ACTUALLY DO ANYTHING BUT FIX THE CAMERA //
    ////////////////////////////////////////////////////////////
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new CameraChange(false));
    }

    public void setCameraBack(boolean front)
    {
        Vision.getInstance().enableBackCamera(front);
    }

}
