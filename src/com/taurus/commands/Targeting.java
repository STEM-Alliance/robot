package com.taurus.commands;

import com.taurus.PIDController;
import com.taurus.robot.Robot;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.command.Command;

public class Targeting extends Command {
    
    private boolean shooterAimed;
    private boolean driveAimed;

    private Vision vision;
    private PIDController drivePID;
    
    public Targeting() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.aimerSubsystem);
        requires(Robot.rockerDriveSubsystem);
        
        vision = Vision.getInstance();        
        drivePID = new PIDController(1, 0, 0, 1); //TODO change max output 
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        shooterAimed = false;
        driveAimed = false;
        
        //setTimeout(1);
        
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        
        shooterAimed = Robot.aimerSubsystem.aim();
        driveAimed = aim(vision.getTarget().Yaw());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return shooterAimed && driveAimed;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
    
    private boolean aim(double changeInAngle) {

        double motorOutput = drivePID.update(changeInAngle);  //TODO add limits for angle

        if(Math.abs(changeInAngle) <= 5){
            Robot.rockerDriveSubsystem.driveRaw(new double[]{0.0, 0.0, 0.0},new double[]{0.0, 0.0, 0.0});
            return true;
        } else {
            Robot.rockerDriveSubsystem.driveRaw(new double[]{-motorOutput, -motorOutput, -motorOutput},
                     new double[]{ motorOutput, motorOutput, motorOutput});
            return false;
        }
    }
}
