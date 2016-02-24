
package com.taurus.commands;

import edu.wpi.first.wpilibj.command.Command;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;

public class DriveTankWithXbox extends Command {
    public DriveTankWithXbox() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.rockerDriveSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Drive", this);
       
        double adjust = .5 + .5 * OI.getThrottleHighSpeed();
        double left = OI.getSpeedLeft() * adjust;
        double right = OI.getSpeedRight() * adjust;
        
        double[] rights = {right,right,right};
        rights[1] = rights[1] * ( 1 + OI.getTractionMiddleIncrease() );
        rights[2] = rights[2] * ( 1 + OI.getTractionMiddleIncrease() );
        
        double[] lefts = {left,left,left};
        lefts[1] = lefts[1] * ( 1 + OI.getTractionMiddleIncrease() );
        lefts[2] = lefts[2] * ( 1 + OI.getTractionMiddleIncrease() );
        
        Robot.rockerDriveSubsystem.driveRaw(rights, lefts, OI.getTractionControl());
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
