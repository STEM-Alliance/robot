package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Rocket League the intake wheels */
public class IntakeManual extends Command
{
    protected final IntakeSubsystem intake = Robot.intakeSubsystem;

    public IntakeManual()
    {
        requires(intake);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Intake", "Manual");
    }

    protected void execute()
    {
        double speed = Robot.controls.getIntakeIn() - Robot.controls.getIntakeOut();  // TODO Should either controller do this?

        if (speed > 0)  // Speed > 0 is exhausting cube
        {
            speed *= 0.8;  // Slow cube out direction
        }

        intake.setIntake(speed);
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        intake.setIntake(0);
    }
}
