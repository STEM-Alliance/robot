package org.wfrobotics.robot.commands;


import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DistanceIntakeSolenoid extends Command{

    double timeSinceLastStateChange;
    protected final RobotState state = RobotState.getInstance();

    public DistanceIntakeSolenoid()
    {
        requires(Robot.intakeSolenoidSubsystem);
    }

    protected void initialize() {

    }

    protected void execute() {
        if (state.intakeSensorReadout <= 10)
        {
            if (timeSinceInitialized() - timeSinceLastStateChange <= 2)
            {
                timeSinceLastStateChange = timeSinceInitialized();
                Robot.intakeSolenoidSubsystem.intakeSolenoidSet(true);
            }
        }
        else
        {
            if (timeSinceInitialized() - timeSinceLastStateChange <= 2)
            {
                timeSinceLastStateChange = timeSinceInitialized();
                Robot.intakeSolenoidSubsystem.intakeSolenoidSet(false);
            }
        }

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
