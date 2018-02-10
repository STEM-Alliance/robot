package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class DistanceIntakeTriggers extends Command
{
    final RobotState state = RobotState.getInstance();
    final IntakeSubsystem intake = Robot.intakeSubsystem;

    public DistanceIntakeTriggers()
    {
        requires(intake);
    }

    protected void execute()
    {
        double speed = Robot.controls.getIntakeIn() - Robot.controls.getIntakeOut();

        intake.update();
        intake.setMotor(speed);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
