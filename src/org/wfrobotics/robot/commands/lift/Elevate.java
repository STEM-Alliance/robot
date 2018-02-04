package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class Elevate extends Command {
    double speed;
    public Elevate() {
        requires(Robot.liftSubsystem);
        speed= .2;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }
    public Elevate(double speed)
    {
        requires(Robot.liftSubsystem);
        this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        Robot.liftSubsystem.setSpeed(speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
