package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class HorizontalIntake extends Command
{
    final boolean high;

    public HorizontalIntake(boolean high)
    {
        requires(Robot.intakeSolenoidSubsystem);
        this.high = high;
    }

    protected void initialize()
    {
        Robot.intakeSolenoidSubsystem.setHorizontal(high);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
