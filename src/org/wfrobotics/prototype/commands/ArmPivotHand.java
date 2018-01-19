package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.utilities.Utilities;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ArmPivotHand extends Command {
    double speed;

    public ArmPivotHand()
    {
        requires(Robot.handSubsystem);
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }
    public ArmPivotHand(double speed)
    {
        requires(Robot.handSubsystem);
        this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {

        Utilities.spinUntilLimit(Robot.handSubsystem.isAtHandCW(),
                Robot.handSubsystem.isAtHandCCW(), speed, Robot.handSubsystem.handMotor);
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
