package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shoot extends CommandGroup
{
    public Shoot(Conveyor.MODE feedMode)
    {
        addSequential(new Rev(Rev.MODE.RAMP, .75));  // Make sure we are at speed before the next step
        addParallel(new Rev(Rev.MODE.SHOOT));
        SmartDashboard.putString("ConveyorMode", feedMode.toString());
        
        addSequential(new Conveyor(feedMode));
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