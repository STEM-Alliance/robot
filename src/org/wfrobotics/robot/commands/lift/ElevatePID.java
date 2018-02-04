package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class ElevatePID extends Command {
    double position;
    public ElevatePID()
    {

        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    public ElevatePID(double position)
    {
        this.position = position;

    }
    // Called just before this Command runs the first time
    protected void initialize()
    {

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
        Robot.liftSubsystem.goToPosition(position);


        SmartDashboard.putNumber("Encoder Error", Robot.liftSubsystem.liftMotorL.getClosedLoopError(0));
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false; //potentially: (Math.abs(Robot.liftSubsystem.getEncoder()- destination) < 30)
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
