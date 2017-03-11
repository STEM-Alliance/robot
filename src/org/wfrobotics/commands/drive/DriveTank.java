
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTank extends Command 
{
    public enum MODE {TANK, ARCADE, OFF}
    
    public final MODE mode;
    boolean backward;
    
    public DriveTank(MODE mode, boolean backward) 
    {
        requires(Robot.driveSubsystem);
        this.mode = mode;
        this.backward = backward;
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {
        double left = 0;
        double right = 0;
        
        Utilities.PrintCommand("Drive", this);
       
        if (mode == MODE.TANK)
        {
            double adjust = Robot.oi.tankOI.getThrottleSpeedAdjust();
            left = Robot.oi.tankOI.getL() * adjust;
            right = Robot.oi.tankOI.getR() * adjust;
    
            if(backward)
            {
                double temp;
                temp = left * -1.0;
                left = right * -1.0;
                right = temp;
            }
            
            // make sure both are non-zero before we try to lock straight
            if(Math.abs(left) > .01 && Math.abs(right) > .01)
            {
                // Assume we want to go straight if similar value
                if (Math.abs(left - right) < .15)
                {
                    double magnitudeAverage = Math.abs((left + right) / 2);
                    left = Math.signum(left) * magnitudeAverage;
                    right = Math.signum(right) * magnitudeAverage;
                }
            }
        }
        else if (mode == MODE.ARCADE)
        {
//            double adjust = Robot.oi.arcadeOI.getThrottleSpeedAdjust();
//            double y = Robot.oi.arcadeOI.getThrottle();
//            double x = Robot.oi.arcadeOI.getTurn() * .8;
//            left = y;  // Default value as if forward/backwards
//            right = y;  // Default value as if forward/backwards
//            
//            Utilities.PrintCommand("Drive", this);
//            
            // this doesn't work
            // Determine if we are instead going left/right
//            if (Math.abs(x) > Math.abs(y))
//            {
//                double direction = Math.signum(x);  // Going Right = 1, Going Left = -1
//                
//                speedL = x * direction;
//                speedR = -x * direction;
//              }
//            
//            left = y + x;
//            right = y - x;
//            left = limit(left) * adjust;
//            right = limit(right) * adjust;
        }

        Robot.driveSubsystem.driveTank(right, left);
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
    
    private double limit(double val)
    {
        double output = val;
        
        if(val > 1)
            output = 1;
        else if (val < -1)
            output= -1;
        return output;
    }
}
