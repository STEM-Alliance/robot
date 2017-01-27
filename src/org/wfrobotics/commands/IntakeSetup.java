package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeSetup extends Command {
    
    boolean on;
    final Intake.MOTOR motor;
    
    public void setOn(boolean on)
    {
        this.on = on;
    }


    public IntakeSetup(boolean on, Intake.MOTOR motor) {
        requires(Robot.intakeSubsystem);
        this.on = on;
        this.motor = motor;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
        double speed = (on) ? 1:0;
        Robot.intakeSubsystem.setSpeed(speed, motor);
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
