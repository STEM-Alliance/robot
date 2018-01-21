package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.utilities.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
        if(Robot.liftSubsystem.BottomSensor.get())
        {
            Robot.liftSubsystem.LiftMotor.setSelectedSensorPosition(0, 0, 0);
        }

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.spinUntilLimit(Robot.liftSubsystem.isAtTop(),Robot.liftSubsystem.isAtBottom(), speed, Robot.liftSubsystem.LiftMotor);


        SmartDashboard.putNumber("LiftEncoder", Robot.liftSubsystem.getEncoder());
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
