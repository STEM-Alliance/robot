package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class FieldRelativeToggle extends Command
{
    public FieldRelativeToggle()
    {
        requires(Robot.driveSubsystem);
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.setFieldRelative(!Robot.driveSubsystem.getFieldRelative());
    }
    
    protected boolean isFinished()
    {
        return true;
    }
}
