package com.taurus.commands;

import com.taurus.robot.Robot;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ShooterFire extends CommandGroup 
{
    private class ShooterFireRev extends CommandGroup
    {
        public ShooterFireRev()
        {
            addParallel(new TargetingDriveAim());
            addSequential(new ShooterRevTimeout(2));
            
        }
    }
    
    private class ShooterFireRelease extends CommandGroup
    {
        public ShooterFireRelease()
        {
            addSequential(new ShooterRelease());
        }
    }
    
    public void initialize()
    {
        //Vision.getInstance().enableBackCamera(false);
    }
    
    public ShooterFire() 
    {
        addSequential(new AimerBetweenAngles(110,125));
        addSequential(new AimerLEDs(true));
        addSequential(new TargetingDriveAim());
        addSequential(new ShooterFireRev());
        addSequential(new ShooterFireRelease());
        addSequential(new AimerLEDs(false));       
    }
    
    public void end()
    {
        Robot.aimerSubsystem.enableLEDs(false);        
        Robot.ballReleaseSubsystem.setBallRelease(false);   
        //Vision.getInstance().enableBackCamera(true);     
    }
    
    public void interrupted()
    {
        end();
    }    
}                           