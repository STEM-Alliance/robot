package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class BoxHold extends Command {

    public BoxHold() {
        requires(Robot.intakeSubsystem);
     
    }

    protected void initialize() {
    }

    protected void execute() {
        double speed = 0.1;
        Robot.intakeSubsystem.setSpeed(speed);
        
    }

    protected boolean isFinished() {
        return false;
    }

    protected void end() {
    }

    protected void interrupted() {
    }
}
