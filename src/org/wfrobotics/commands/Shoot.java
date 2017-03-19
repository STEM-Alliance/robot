package org.wfrobotics.commands;

import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shoot extends CommandGroup
{
    public Shoot(Conveyor.MODE feedMode)
    {
        this(feedMode, Constants.AUGER_SPEED, Constants.AUGER_UNJAM_SPEED);
    }
    
    public Shoot(Conveyor.MODE feedMode, double timeout)
    {
        this(feedMode, Constants.AUGER_SPEED, Constants.AUGER_UNJAM_SPEED, timeout);
    }
    
    public Shoot(Conveyor.MODE feedMode, double speedFeed, double speedUnjam)
    {
        addSequential(new Rev(Rev.MODE.RAMP, 1));  // Make sure we are at speed before the next step
        addParallel(new Rev(Rev.MODE.SHOOT));
        SmartDashboard.putString("ConveyorMode", feedMode.toString());
        
        addSequential(new Conveyor(feedMode, speedFeed, speedUnjam));
    }
    
    public Shoot(Conveyor.MODE feedMode, double speedFeed, double speedUnjam, double timeout)
    {
        addSequential(new Rev(Rev.MODE.RAMP, .75));  // Make sure we are at speed before the next step
        addParallel(new Rev(Rev.MODE.SHOOT));
        SmartDashboard.putString("ConveyorMode", feedMode.toString());
        
        addSequential(new Conveyor(feedMode, speedFeed, speedUnjam));
        
        setTimeout(timeout);
    }
    
    protected void end()
    {
        Robot.shooterSubsystem.topThenBottom(0,  Constants.SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM);
        Robot.augerSubsystem.setSpeed(0);
    }
    
    protected void interrupted()
    {
        end();
    }
    
    protected boolean isFinished()
    {
        return isTimedOut();
    }
}