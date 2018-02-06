package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeSolenoid extends Command {
    boolean high;
    boolean usesTrue;

    public IntakeSolenoid ()
    {
        requires(Robot.intakeSolenoidSubsystem);
    }
    public IntakeSolenoid(boolean high)
    {
        usesTrue = true;
        requires(Robot.intakeSolenoidSubsystem);
        this.high = high;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {if(usesTrue)
    {

        Robot.intakeSolenoidSubsystem.intakeSolenoidSet(high);
    }
    else
    {
        Robot.intakeSolenoidSubsystem.intakeSolenoidSet(!Robot.intakeSolenoidSubsystem.getGrab());

    }
    }


    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
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
