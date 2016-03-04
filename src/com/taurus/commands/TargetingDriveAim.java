package com.taurus.commands;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.robot.Robot;
import com.taurus.vision.Target;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TargetingDriveAim extends Command
{    
    public final double DRIVE_ANGLE_TOLERANCE = .8;
    public final int HOLD_COUNT = 3;

    private int shooterAimed;
    private int driveAimed;

    private Vision vision;
    private PIDController drivePID;

    private double startTime;
    
    public TargetingDriveAim() 
    {
        requires(Robot.aimerSubsystem);
        requires(Robot.rockerDriveSubsystem);
        
        vision = Vision.getInstance();        
        drivePID = new PIDController(.2, 0.1, 0.1, .75); //TODO change max output 
    }

    protected void initialize() 
    {
        shooterAimed = 0;
        driveAimed = 0;

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
                if(Robot.aimerSubsystem.aim(target))
                    shooterAimed++;
                else
                    shooterAimed = 0;
                
                if(aim(target.Yaw()))
                    driveAimed++;
                else
                    driveAimed = 0; 
            }
            else
            {
                shooterAimed = 0;
                driveAimed = 0;//aim(0);
            }

            SmartDashboard.putBoolean("TargetFound", target != null);
            SmartDashboard.putBoolean("TargetAimPitch", shooterAimed > HOLD_COUNT);
            SmartDashboard.putBoolean("TargetAimYaw", driveAimed > HOLD_COUNT);
            //SmartDashboard.putString("Targeting", "" + (target != null) + driveAimed + shooterAimed);
        }
    }

    protected boolean isFinished() 
    {
        return shooterAimed > HOLD_COUNT && driveAimed > HOLD_COUNT;
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
        updatedPIDConstants();
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

    private void updatedPIDConstants()
    {

        drivePID.setP(Preferences.getInstance().getDouble("DrivePID_P", .2));
        drivePID.setI(Preferences.getInstance().getDouble("DrivePID_I", 0));
        drivePID.setD(Preferences.getInstance().getDouble("DrivePID_D", 0));
        drivePID.setMax(Preferences.getInstance().getDouble("DrivePID_Max", 0.7));
    }
}
