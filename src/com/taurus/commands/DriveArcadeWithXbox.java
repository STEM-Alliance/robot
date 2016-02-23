
package com.taurus.commands;

import edu.wpi.first.wpilibj.command.Command;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;

public class DriveArcadeWithXbox extends Command 
{
    public DriveArcadeWithXbox() 
    {
        requires(Robot.rockerDriveSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() 
    {
        
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {        
        double y = OI.getThrottleY();
        double x = OI.getThrottleX();
        double speedL = y;  // Default value as if forward/backwards
        double speedR = y;  // Default value as if forward/backwards
        
        Utilities.PrintCommand("Drive", this);
        
        // this doesn't work
//        // Determine if we are instead going left/right
//        if (Math.abs(x) > Math.abs(y))
//        {
//            double direction = Math.signum(x);  // Going Right = 1, Going Left = -1
//            
//            speedL = x * direction;
//            speedR = -x * direction;
//        }
        
        speedL = y + x;
        speedR = y - x;
        speedL = limit(speedL);
        speedR = limit(speedR);
        
        Robot.rockerDriveSubsystem.driveRaw(speedR, speedL, OI.getTractionControl());
    }
    
    private double limit(double val)
    {
        double output = val;
        
        if(val > 1)
            output = 1;
        else if (val < -1)
            output= -1;
        return output;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;  // Always run this command because it will be default command of the subsystem.
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
