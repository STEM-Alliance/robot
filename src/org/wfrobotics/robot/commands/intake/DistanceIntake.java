package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class DistanceIntake extends Command
{
    final RobotState state = RobotState.getInstance();
    final IntakeSubsystem intake = Robot.intakeSubsystem;

    /**
     * This command gets the distance from the subsystem and based on that distance
     * drive the motors at different speeds
     */
    public DistanceIntake()
    {
        requires(intake);
    }

    protected void execute()
    {
        double speed = 0.1;

        intake.update();

        if (state.intakeSensorReadout <= IntakeSubsystem.kDistanceHasCube)
        {
            speed = 0.0;
        }
        intake.setMotor(speed);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
