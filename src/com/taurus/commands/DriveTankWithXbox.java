
package com.taurus.commands;

import edu.wpi.first.wpilibj.command.Command;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;

public class DriveTankWithXbox extends Command 
{
    boolean backward;
    
    public DriveTankWithXbox(boolean backward) 
    {
        requires(Robot.rockerDriveSubsystem);
        this.backward = backward;
    }

    protected void initialize() 
    {
        Robot.rockerDriveSubsystem.enableGyro(false);
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
       
        double adjust = 1.0 - .5 * OI.getThrottleHighSpeed();
        double left = OI.getSpeedLeft() * adjust;
        double right = OI.getSpeedRight() * adjust;

        if(backward)
        {
            double temp;
            temp = left * -1.0;
            left = right * -1.0;
            right = temp;
        }
        
        // Assume we want to go straight if at high speed and of similar value
        if (Math.abs(left - right) < .1 && Math.abs(left) > .5 && Math.abs(right) > .5)
        {
            double magnitudeAverage = Math.abs((left + right) / 2);
            left = Math.signum(left) * magnitudeAverage;
            right = Math.signum(right) * magnitudeAverage;
        }
        
        Robot.rockerDriveSubsystem.driveRaw(right, left);
    }

    protected boolean isFinished() 
    {
        return false;  // Always run this command because it will be default command of the subsystem.
    }

    protected void end() 
    {
    }

    protected void interrupted() 
    {
    }
}
