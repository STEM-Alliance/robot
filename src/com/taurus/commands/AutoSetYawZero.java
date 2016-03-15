package com.taurus.commands;

import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class AutoSetYawZero extends Command {
    
    private double angle;
    
    public AutoSetYawZero(double angle) { 
        requires(Robot.rockerDriveSubsystem);
        this.angle = angle;
    }

    protected void initialize() {
        Robot.rockerDriveSubsystem.zeroYaw(angle);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
