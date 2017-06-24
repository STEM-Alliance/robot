package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TrackTarget extends Command {

    boolean hasTarget;
    
    public TrackTarget()
    {
        requires(Robot.camServoSubsystem);
    }
    int value = 0;
    
    @Override
    protected void execute()
    {
        super.execute();
        
        if ( Robot.camTrackSubsystem.getHasTarget())
        {
            SmartDashboard.putNumber("Xoffset Value!", Robot.camTrackSubsystem.getXoffset());
            SmartDashboard.putNumber("Yoffset Value!", Robot.camTrackSubsystem.getYoffset());

            Robot.camServoSubsystem.setX(Robot.camTrackSubsystem.getXoffset());
            Robot.camServoSubsystem.setY(-(Robot.camTrackSubsystem.getYoffset()));
            
        }
        else
        {
            Robot.camServoSubsystem.setX(0);
            Robot.camServoSubsystem.setY(0);
        }
        
        SmartDashboard.putBoolean("TrackTargetBool", Robot.camTrackSubsystem.getHasTarget());
    }
    @Override
    protected boolean isFinished()
    {
        return false;
    }

}
