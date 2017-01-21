package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;


public class Shoot extends Command {
    boolean start;
   
     //boolean ballIn;
    
    public Shoot(boolean on) {
        requires(Robot.shooterSubsystem);
        start = on;
    }
    //public ballIn (boolean on){
        /*get the sensor value
           if the sensor value says good
               ballIn is true
           else
               ballIn is false
        */
        //ballIn = on;
    //}
 
 // Called just before this Command runs the first time
    protected void initialize() {
  
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        boolean atSpeed = Robot.shooterSubsystem.speedReached(100);
        if(start && atSpeed /* && targeting && hasBall*/){
                   
          //shoot not fully designed yet
            //could be a servo pushing a ball through
            
        }
        else{
            Robot.shooterSubsystem.setSpeed(3800); 
        }
        
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }

   
}
