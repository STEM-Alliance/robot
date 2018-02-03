package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DistanceIntakePush extends Command
{
    double speed;

    public DistanceIntakePush()
    {
        requires(Robot.intakeSubsystem);
        speed = .1;
    }

    public DistanceIntakePush(double speed)
    {
        requires(Robot.intakeSubsystem);
        this.speed = speed;
    }

    protected void initialize()
    {
    }

    protected void execute()
    {
        if (Robot.intakeSubsystem.getDistance() >= 35)
        {
            Robot.intakeSubsystem.setMotor(0);
            Robot.intakeSubsystem.pushHasCube(false);;
        }
        else
        {
            Robot.intakeSubsystem.setMotor(0.2);
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
