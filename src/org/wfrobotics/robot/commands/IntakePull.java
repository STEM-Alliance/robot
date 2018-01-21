package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.utilities.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakePull extends Command
{
    double speed;

    public IntakePull()
    {
        requires(Robot.intakeSubsystem);
        speed = .5;        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    public IntakePull(double speed)
    {
        requires(Robot.intakeSubsystem);
        this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        // if speed control is desired, readd for operator controller
        //        if (Robot.controls.getTriggerRotationMan() != 0)
        //        {
        //            Utilities.spinUntilLimit(false, false, speed * Robot.controls.getTriggerRotationMan(), Robot.intakeSubsystem.rightIntake);
        //        }
        //        else
        {
            Utilities.spinUntilLimit(false, false, speed, Robot.intakeSubsystem.rightIntake);
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return true;
    }

    // Called once after isFinished returns true
    protected void end()
    {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    }
}
