package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.CameraServos;

import edu.wpi.first.wpilibj.command.Command;

public class XboxGimbal extends Command {
    
    CameraServos servoX,
                 servoY;
    public XboxGimbal()
    {
        requires(Robot.camServoSubsystem);
      
        servoX = new CameraServos();
        servoY = new CameraServos();
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
        // found in bottom of the Robot IO class 
        
        Robot.camServoSubsystem.setX(Robot.oi.getJoyX());
        Robot.camServoSubsystem.setY(Robot.oi.getJoyY());
        
    }    
}
