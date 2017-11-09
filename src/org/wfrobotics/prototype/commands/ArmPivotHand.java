package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.prototype.subsystems.ArmSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ArmPivotHand extends Command {
    double speed;
    public ArmPivotHand() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }
    public ArmPivotHand(double speed)
    {
        this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        this.speed = Robot.controls.getTriggerRotation();

        Robot.armSubsystem.spinUntilLimit(ArmSubsystem.isAtHandCW(),
                ArmSubsystem.isAtHandCCW(), speed, ArmSubsystem.handMotor);
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
