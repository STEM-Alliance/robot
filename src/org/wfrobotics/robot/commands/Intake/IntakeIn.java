package org.wfrobotics.robot.commands.Intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeIn extends Command {
   
    
    
    public IntakeIn() 
    {
        requires(Robot.intakeSubsystem);
    }

    protected void initialize() {
    }

    protected void execute() {
        
        double speed = 0.2;
        Robot.intakeSubsystem.setSpeed(speed); 
        // do we need to still spin the motor?
        Robot.intakeSubsystem.setSpeed(0);
        
    }

    protected boolean isFinished() {
        return false;
    }

    protected void end() {
    }

    protected void interrupted() {
    }
}
