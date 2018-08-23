package org.wfrobotics.robot.auto;

import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/** Set the jaw solenoids open or closed to nom nom nom dat cube */
public class IntakeSet extends Command
{
    private final IntakeSubsystem intake = IntakeSubsystem.getInstance();
    private final double speed;

    public IntakeSet(double percentageOutward, double timeout, boolean blockIntake)
    {
        if (blockIntake)  // Don't allow SmartIntake to override this autonomous command
        {
            requires(intake);
        }
        speed = percentageOutward;
        setTimeout(timeout);
    }

    protected void initialize()
    {
        intake.setIntake(speed);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end()
    {
        intake.setIntake(0.0);
    }
}
