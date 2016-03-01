package com.taurus.commands;

import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ShooterFire extends CommandGroup {

    private class ShooterFireRev extends CommandGroup
    {
        public ShooterFireRev()
        {
//            addParallel(new TargetingDriveAim());
//            addSequential(new ShooterRevTimeout());
            addParallel(new ShooterRevTimeout(2));
            addSequential(new TargetingDriveAim());
            
        }
    }
    
    private class ShooterFireRelease extends CommandGroup
    {
        public ShooterFireRelease()
        {
//            addParallel(new TargetingDriveAim());
//            addSequential(new ShooterRelease());
            addParallel(new ShooterRelease());
            addSequential(new TargetingDriveAim());
        }
    }
    
    public ShooterFire() {
        addSequential(new AimerLEDs(true));
        addSequential(new ShooterFireRev());
        addSequential(new ShooterFireRelease());
        addSequential(new AimerLEDs(false));
        
    }
    
    public void end()
    {
        Robot.aimerSubsystem.enableLEDs(false);
        
        Robot.ballReleaseSubsystem.setBallRelease(false);
        
    }
    
    public void interrupted()
    {
        end();
    }
    
}                           