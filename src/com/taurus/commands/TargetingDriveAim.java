package com.taurus.commands;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.robot.Robot;
import com.taurus.vision.Target;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TargetingDriveAim extends Command {
    
    public final double DRIVE_ANGLE_TOLERANCE = 2;
    
    private boolean shooterAimed;
    private boolean driveAimed;

    private Vision vision;
    private PIDController drivePID;

    private double startTime;
    
    public TargetingDriveAim() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.aimerSubsystem);
        requires(Robot.rockerDriveSubsystem);
        
        vision = Vision.getInstance();        
        drivePID = new PIDController(.2, 0, 0, 1); //TODO change max output 
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        shooterAimed = false;
        driveAimed = false;

        startTime = Timer.getFPGATimestamp();
        
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Aimer", this);
        Utilities.PrintCommand("Drive", this);
        
        if(Timer.getFPGATimestamp() - startTime > .25)
        {
            shooterAimed = Robot.aimerSubsystem.aim();
            
            Target target = vision.getTarget();
            if(target != null)
            {
                driveAimed = aim(target.Yaw());
            }
            else
            {
                driveAimed = aim(0);
            }
            
            SmartDashboard.putString("Targeting", "" + (target != null) + driveAimed + shooterAimed);
        }
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

        if(Math.abs(changeInAngle) <= DRIVE_ANGLE_TOLERANCE){
            Robot.rockerDriveSubsystem.driveRaw(0.0, 0.0, false);
            return true;
        } else {
            Robot.rockerDriveSubsystem.driveRaw(-motorOutput, motorOutput, false);
            return false;
        }
    }
}
