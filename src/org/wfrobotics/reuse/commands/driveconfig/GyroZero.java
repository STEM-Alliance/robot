package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class GyroZero extends Command
{
    public GyroZero()
    {
        requires(Robot.driveSubsystem);
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.zeroGyro();        
    }

    @Override
    protected boolean isFinished()
    {
        return true;
    }
}
