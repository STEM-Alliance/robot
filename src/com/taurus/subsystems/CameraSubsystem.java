package com.taurus.subsystems;

import com.taurus.commands.CameraFix;

import edu.wpi.first.wpilibj.command.Subsystem;

public class CameraSubsystem extends Subsystem {

    ////////////////////////////////////////////////////////////
    // THIS SHOULDN'T ACTUALLY DO ANYTHING BUT FIX THE CAMERA //
    ////////////////////////////////////////////////////////////
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new CameraFix());
    }

}
