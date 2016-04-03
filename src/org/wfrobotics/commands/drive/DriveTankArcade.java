
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTankArcade extends Command 
{
    public DriveTankArcade() 
    {
        requires(Robot.tankDriveSubsystem);
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {

        double adjust = OI.DriveArcadeOI.getThrottleSpeedAdjust();
        double y = OI.DriveArcadeOI.getThrottle();
        double x = OI.DriveArcadeOI.getTurn() * .8;
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
        
        Robot.tankDriveSubsystem.driveRaw(speedR * adjust, speedL * adjust);
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
