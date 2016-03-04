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
//            addParallel(new TargetingDriveAim());
//            addSequential(new ShooterRevTimeout());
            addSequential(new TargetingDriveAim());
            
        }
    }
    
    private class ShooterFireRelease extends CommandGroup
    {
        public ShooterFireRelease()
        {
//            addParallel(new TargetingDriveAim());
//            addSequential(new ShooterRelease());
            addSequential(new ShooterRevTimeout(2));
            addSequential(new ShooterRelease());
            //addSequential(new TargetingDriveAim());
        }
    }
    
    public void initialize()
    {
        //Vision.getInstance().enableBackCamera(false);
    }
    
    public ShooterFire() 
    {
        addSequential(new AimerBetweenAngles(90,120));
        addSequential(new AimerLEDs(true));
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