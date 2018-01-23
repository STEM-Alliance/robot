package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class ElevateRendezvous extends Command {
    double speed;
    int destination;
    public ElevateRendezvous() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }
    public ElevateRendezvous(int destination,double speed)
    {
        requires(Robot.liftSubsystem);
        this.speed = speed;
        this.destination = destination;
    }

    // Called just before this Command runs the first time
    protected void initialize() {

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        if((!(Math.abs(Robot.liftSubsystem.getEncoder()- destination) < 100)))
        {

            if (Robot.liftSubsystem.getEncoder()<= destination)
            {

                Robot.liftSubsystem.setSpeed(speed);

            }
            else {
                Robot.liftSubsystem.setSpeed(-speed);

            }
        }

        SmartDashboard.putNumber("LiftEncoder", Robot.liftSubsystem.getEncoder());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return (Math.abs(Robot.liftSubsystem.getEncoder()- destination) < 100);
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
