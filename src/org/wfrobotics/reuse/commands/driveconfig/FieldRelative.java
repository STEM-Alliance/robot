package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class FieldRelative extends Command
{
    private boolean request = false;
    
    public FieldRelative(boolean enable)
    {
        requires(Robot.driveSubsystem);
        this.request = enable;
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.setFieldRelative(request);
    }
    
    protected boolean isFinished()
    {
        return true;
    }
}
