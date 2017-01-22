package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class Shoot extends CommandGroup
{
    public Shoot(Feed.MODE feedMode)
    {
        addParallel(new Rev(Constants.SHOOTER_READY_SHOOT_SPEED));
        addSequential(new Feed(feedMode));
    }
    
    protected void end()
    {
        Robot.shooterSubsystem.setSpeed(0);
    }
    
    protected void interrupted()
    {
        end();
    }
}