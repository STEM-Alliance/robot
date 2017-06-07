package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.reuse.controller.XboxJoystickButton;
import org.wfrobotics.robot.subsystems.CameraServos;

import edu.wpi.first.wpilibj.command.Command;

public class XboxGimbal extends Command {
    
    CameraServos serX,
                 serY;
    public XboxGimbal()
    {
        serX = new CameraServos();
        serY = new CameraServos();
    }
    
    @Override
    protected boolean isFinished()
    {
        return false;
    }
    @Override
    protected void execute()
    {
        super.execute();
        
        
    }
    

}
