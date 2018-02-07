package org.wfrobotics.robot.commands.intake;


import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;


public class DistanceIntakeSolenoid extends Command{

    double timeSinceLastStateChange;
    protected final RobotState state = RobotState.getInstance();
    public int releseDistance;
    /**
     *
     * @param release distance from block in cm
     */
    public DistanceIntakeSolenoid(int release)
    {
        requires(Robot.intakeSolenoidSubsystem);
        releseDistance = release;
    }

    protected void initialize() {

    }

    protected void execute() {
        if (state.intakeSensorReadout <= releseDistance)
        {
            if (timeSinceInitialized() - timeSinceLastStateChange <= 2)
            {
                timeSinceLastStateChange = timeSinceInitialized();
                Robot.intakeSolenoidSubsystem.setHorizontal(true);
            }
        }
        else
        {
            if (timeSinceInitialized() - timeSinceLastStateChange <= 2)
            {
                timeSinceLastStateChange = timeSinceInitialized();
                Robot.intakeSolenoidSubsystem.setHorizontal(false);
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
