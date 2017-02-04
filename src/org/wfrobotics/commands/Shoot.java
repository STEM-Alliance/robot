package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class Shoot extends CommandGroup
{
    public Shoot(Conveyor.MODE feedMode)
    {
        addParallel(new Rev(Rev.MODE.SHOOT));
        addSequential(new Conveyor(feedMode));
    }
    
    // TODO DRL in execute, we could unjam if our sensor has not detected a ball shot after some time
    
    protected void end()
    {
        Robot.shooterSubsystem.setSpeed(0);
        Robot.augerSubsystem.setSpeed(0);
    }
    
    protected void interrupted()
    {
        end();
    }
}