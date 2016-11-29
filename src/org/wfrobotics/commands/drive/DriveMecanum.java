package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.OI.DriveMecanumOI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.MecanumDriveSubsystem;

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
       
        
        
        Robot.mecanumDriveSubsystem.drive(DriveMecanumOI.getX()/2.0, DriveMecanumOI.getY()/2.0, DriveMecanumOI.getRotation()/2.0);
        
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
