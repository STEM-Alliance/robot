package org.wfrobotics.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class LEDSet extends InstantCommand{

    double value = 0;
    public LEDSet()
    {
        //        requires(Robot.ledSubsystem);
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }
    public LEDSet(double value) {

        //        requires(Robot.ledSubsystem);
        this.value = value;
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {

    }


    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        //        Robot.ledSubsystem.LEDSet(value);

    }


    // Called once after isFinished returns true
    protected void end() {

    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
