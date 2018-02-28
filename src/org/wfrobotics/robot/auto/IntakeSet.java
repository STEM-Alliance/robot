package org.wfrobotics.robot.auto;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/** Set the jaw solenoids open or closed to nom nom nom dat cube */
public class IntakeSet extends Command
{
    private final double speed;

    public IntakeSet(double percentageOutward, double timeout, boolean blockIntake)
    {
        if (blockIntake)  // Don't allow SmartIntake to override this autonomous command
        {
            requires(Robot.intakeSubsystem);
        }
        speed = percentageOutward;
        setTimeout(timeout);
    }

    protected void initialize()
    {
        Robot.intakeSubsystem.setMotor(speed);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end()
    {
        Robot.intakeSubsystem.setMotor(0);
    }
}
