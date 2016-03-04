
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
        
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
       
        double adjust = .8 + .2 * OI.getThrottleHighSpeed();
        double left = OI.getSpeedLeft() * adjust;
        double right = OI.getSpeedRight() * adjust;

        if(backward)
        {
            double temp;
            temp = left * -1.0;
            left = right * -1.0;
            right = temp;
        }
        
        
        double[] rights = {right,right,right};
        rights[1] = rights[1] * ( 1 + OI.getTractionMiddleIncrease() );
        rights[2] = rights[2] * ( 1 + OI.getTractionMiddleIncrease() );
        
        double[] lefts = {left,left,left};
        lefts[1] = lefts[1] * ( 1 + OI.getTractionMiddleIncrease() );
        lefts[2] = lefts[2] * ( 1 + OI.getTractionMiddleIncrease() );
        
        Robot.rockerDriveSubsystem.driveRaw(rights, lefts, OI.getTractionControl());
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
