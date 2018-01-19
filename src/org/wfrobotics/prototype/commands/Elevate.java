package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.prototype.subsystems.LiftSubsystem;
import org.wfrobotics.reuse.utilities.Utilities;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class Elevate extends Command {
    double speed;
    public Elevate() {
        requires(Robot.liftSubsystem);
        this.speed= .2;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }
    public Elevate(double speed)
    {
        requires(Robot.liftSubsystem);
        this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.spinUntilLimit(LiftSubsystem.isAtTop(),LiftSubsystem.isAtBottom(), speed, Robot.liftSubsystem.LiftMotor);
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
