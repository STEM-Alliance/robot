package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.OI.DriveMecanumOI;
import org.wfrobotics.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveMecanum extends Command {

    public DriveMecanum() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.mecanumDriveSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
       
        
        
        Robot.mecanumDriveSubsystem.drive(-DriveMecanumOI.getX()*0.7, DriveMecanumOI.getY()*0.7, -DriveMecanumOI.getRotation()*0.7);
        
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
