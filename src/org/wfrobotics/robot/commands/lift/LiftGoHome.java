package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.command.Command;

public class LiftGoHome extends Command
{
    private final Lift lift = Lift.getInstance();
    private final double speed;

    public LiftGoHome(double speed, double timeout)
    {
        requires(lift);
        this.speed = speed;
        setTimeout(timeout);
    }

    protected void initialize()
    {
        lift.setOpenLoop(speed);
    }

    protected boolean isFinished()
    {
        final boolean override = Robot.controls.isLiftOverrideRequested();
        return lift.hasZeroed() || isTimedOut() || override;
    }

    protected void end()
    {
        lift.setOpenLoop(0.0);
    }
}
