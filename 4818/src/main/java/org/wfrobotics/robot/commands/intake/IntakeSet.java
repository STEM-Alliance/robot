package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

/** Set the jaw solenoids open or closed to nom nom nom dat cube */
public class IntakeSet extends Command
{
    private final Intake intake = Intake.getInstance();
    private final double speed;

    public IntakeSet(double percentageOutward, double timeout)
    {
        requires(intake);
        speed = percentageOutward;
        setTimeout(timeout);
    }

    protected void initialize()
    {
        intake.setMotors(speed);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end()
    {
        intake.setMotors(0.0);
    }
}
