package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveToggleFieldRelative extends Command {

    public DriveToggleFieldRelative() 
    {
        requires(Robot.driveSubsystem);
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.setFieldRelative(!Robot.driveSubsystem.getFieldRelative());
    }
    
    @Override
    protected boolean isFinished()
    {
        return true;
    }

}
