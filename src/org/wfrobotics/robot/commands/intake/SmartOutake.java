package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartOutake extends Command
{
    protected final IntakeSubsystem intake = Robot.intakeSubsystem;

    public SmartOutake()
    {
        requires(intake);
        setTimeout(.5);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Intake", "Smart Out");
    }

    protected void execute()
    {
        intake.setIntake(0.25);
        if (timeSinceInitialized() > .25)
        {
            intake.setHorizontal(true);
        }
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
