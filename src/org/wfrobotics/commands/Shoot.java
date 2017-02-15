package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class Shoot extends CommandGroup
{
    public Shoot(Conveyer.MODE feedMode)
    {
        addParallel(new Rev(Rev.MODE.SHOOT));
        
        addSequential(new Conveyer(feedMode));
    }
    
    // TODO DRL in execute, we could unjam if our sensor has not detected a ball shot after some time
    
    protected void end()
    {
        Robot.shooterSubsystem.topThenBottom(0,  Constants.SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM);

        Robot.augerSubsystem.setSpeed(0);
    }
    
    protected void interrupted()
    {
        end();
    }
}