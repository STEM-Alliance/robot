package com.taurus.commands;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.robot.Robot;
import com.taurus.vision.Target;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TargetingDriveAim extends Command
{    
    public final double DRIVE_ANGLE_TOLERANCE = 1;
    
    private boolean shooterAimed;
    private boolean driveAimed;

    private Vision vision;
    private PIDController drivePID;

    private double startTime;
    
    public TargetingDriveAim() 
    {
        requires(Robot.aimerSubsystem);
        requires(Robot.rockerDriveSubsystem);
        
        vision = Vision.getInstance();        
        drivePID = new PIDController(.2, 0, 0, .4); //TODO change max output 
    }

    protected void initialize() 
    {
        shooterAimed = false;
        driveAimed = false;

        startTime = Timer.getFPGATimestamp();        
    }

    protected void execute()
    {
        Utilities.PrintCommand("Aimer", this);
        Utilities.PrintCommand("Drive", this);
        
        if(Timer.getFPGATimestamp() - startTime > .25)
        {
            Target target = vision.getTarget();

            if(target != null)
            {   
                shooterAimed = Robot.aimerSubsystem.aim(target);
                driveAimed = aim(target.Yaw());
            }
            else
            {
                shooterAimed = false;
                driveAimed = false;//aim(0);
            }

            SmartDashboard.putBoolean("TargetFound", target != null);
            SmartDashboard.putBoolean("TargetAimPitch", shooterAimed);
            SmartDashboard.putBoolean("TargetAimYaw", driveAimed);
            //SmartDashboard.putString("Targeting", "" + (target != null) + driveAimed + shooterAimed);
        }
    }

    protected boolean isFinished() 
    {
        return shooterAimed && driveAimed;
    }

    protected void end()
    {
        
    }

    protected void interrupted() 
    {
        end();
    }
    
    private boolean aim(double changeInAngle) 
    {
        double motorOutput = -drivePID.update(changeInAngle);  //TODO add limits for angle

        if(Math.abs(changeInAngle) <= DRIVE_ANGLE_TOLERANCE)
        {
            Robot.rockerDriveSubsystem.driveRaw(0.0, 0.0, false);
            return true;
        } 
        else
        {
            Robot.rockerDriveSubsystem.driveRaw(-motorOutput, motorOutput, false);
            return false;
        }
    }
}
