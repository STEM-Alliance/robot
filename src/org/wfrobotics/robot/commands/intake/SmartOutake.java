package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartOutake extends Command
{
    protected final RobotState state = RobotState.getInstance();
    protected final IntakeSubsystem intake = Robot.intakeSubsystem;

    public SmartOutake()
    {
        requires(intake);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Intake", "Smart Out");
    }

    protected void execute()
    {
        if (state.intakeDistance < 6.5)
        {
            intake.setIntake(0.35);
        }
        else {
            intake.setHorizontal(true);
        }

    }

    protected boolean isFinished()
    {
        return !state.robotHasCube;
    }
}
