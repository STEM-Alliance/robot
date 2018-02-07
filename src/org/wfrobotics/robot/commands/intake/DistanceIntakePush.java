package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DistanceIntakePush extends Command
{
    final RobotState state = RobotState.getInstance();
    final IntakeSubsystem intake = Robot.intakeSubsystem;

    public DistanceIntakePush()
    {
        requires(intake);
    }

    protected void execute()
    {
        intake.update();
        Robot.intakeSubsystem.setMotor(0.2);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
