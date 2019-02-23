package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.VisionProcessor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class VisionTest extends Command
{
    public VisionTest()
    {
        
    }

    public void onBackgroundUpdate() 
    {
        
    }

    public boolean isFinished()
    {
        return false;
    }    
}