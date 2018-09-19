package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/** Rocket League the intake wheels */
public class IntakeManual extends Command
{
    protected final IntakeSubsystem intake = IntakeSubsystem.getInstance();

    public IntakeManual()
    {
        requires(intake);
    }

    protected void execute()
    {
        double speed = Robot.controls.getIntakeIn() - Robot.controls.getIntakeOut();  // TODO Should either controller do this?

        if (speed > 0)  // Speed > 0 is exhausting cube
        {
            speed *= 0.8;  // Slow cube out direction
        }

        intake.setMotors(speed);
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        intake.setMotors(0);
    }
}
