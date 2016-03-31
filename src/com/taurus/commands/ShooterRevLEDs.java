package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class ShooterRevLEDs extends CommandGroup 
{
    public void initialize()
    {
    }
    
    public ShooterRevLEDs() 
    {
        addSequential(new AimerLEDs(true));
        addSequential(new ShooterRev());
    }
    
    public void end()
    {
        Robot.ledsSubsystem.enableLEDs(false);
    }
    
    public void interrupted()
    {
        end();
    }

}                           