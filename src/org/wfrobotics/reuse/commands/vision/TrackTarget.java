package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class TrackTarget extends Command {

    boolean hasTarget;
    
    public TrackTarget()
    {
        requires(Robot.camServoSubsystem);
    }
    
    @Override
    protected void execute()
    {
        super.execute();
        
        Robot.camServoSubsystem.setX(Robot.camServoSubsystem.getXoffset());
        Robot.camServoSubsystem.setY(Robot.camServoSubsystem.getYoffset());
        
    }
    @Override
    protected boolean isFinished()
    {
        return false;
    }

}
