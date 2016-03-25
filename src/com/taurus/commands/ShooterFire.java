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
        addParallel(new DriveBrakeMode(true));
        addSequential(new AimerLEDs(true));
        addSequential(new AimerBetweenAngles(110,125));
        addSequential(new TargetingDriveAim());
        addSequential(new ShooterFireRev());
        addSequential(new ShooterFireRelease());
        addParallel(new AimerLEDs(false));
        addSequential(new DriveBrakeMode(false));
    }
    
    public ShooterFire(boolean shoot)
    {
        addParallel(new DriveBrakeMode(true));
        addSequential(new AimerLEDs(true));
        addSequential(new AimerBetweenAngles(110,125));
        addSequential(new TargetingDriveAim());
        if (shoot)
        {
            addSequential(new ShooterFireRev());
            addSequential(new ShooterFireRelease());
        }
        addParallel(new AimerLEDs(false));
        addSequential(new DriveBrakeMode(false));
    }
    
    public void end()
    {
        Robot.aimerSubsystem.enableLEDs(false);        
        Robot.ballReleaseSubsystem.setBallRelease(false);
        //Vision.getInstance().enableBackCamera(true);     
    }
    
    public void interrupted()
    {
        Robot.rockerDriveSubsystem.setBrakeMode(false);
        end();
    }    
}                           