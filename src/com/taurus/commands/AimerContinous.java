package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class AimerContinous extends Command {

    final double totalTime = 2;
    
    double startTime;
    
    final double speedMin = .2;
    final double speedMax = .5;
    
    boolean clockwise;
    
    double speed;
    
    public AimerContinous(boolean clockwise) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.aimerSubsystem);
        
        this.clockwise = clockwise;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
        speed = speedMin;
        startTime = Timer.getFPGATimestamp();
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        
        Utilities.PrintCommand("Aimer", this);
        
        // increase speed over a set amount of time to reach max speed while button is held
        double percent = ((Timer.getFPGATimestamp() - startTime) / totalTime);
        speed = percent * (speedMax - speedMin) + speedMin;
        
        
        if(clockwise)
            Robot.aimerSubsystem.setSpeed(speed);
        else
            Robot.aimerSubsystem.setSpeed(-speed);
            
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
    
    // Called once after isFinished returns true
    protected void end() {
      Robot.aimerSubsystem.setSpeed(0);
      
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}