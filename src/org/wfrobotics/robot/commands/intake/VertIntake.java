package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class VertIntake extends Command
{
    boolean high;

    public VertIntake(boolean high)
    {
        requires(Robot.intakeSolenoidSubsystem);
        this.high = high;
    }
    protected void initialize()
    {

    }

    protected boolean isFinished()
    {
        return false;
    }
}
