package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class LiftGoHome extends Command
{
    private final LiftSubsystem lift = LiftSubsystem.getInstance();
    private final double speed;

    public LiftGoHome(double speed, double timeout)
    {
        requires(lift);
        this.speed = speed;
        setTimeout(timeout);
    }

    protected void execute()
    {
        lift.setOpenLoop(speed);
    }

    protected boolean isFinished()
    {
        return lift.hasZeroed() || isTimedOut() || lift.AtHardwareLimitTop();
    }

    protected void end()
    {
        lift.setOpenLoop(0.0);
    }
}
