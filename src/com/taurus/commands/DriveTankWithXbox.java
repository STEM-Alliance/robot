
package com.taurus.commands;

import edu.wpi.first.wpilibj.command.Command;

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
        Robot.rockerDriveSubsystem.tankDrive(OI.getSpeedRight(), OI.getSpeedLeft());
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
