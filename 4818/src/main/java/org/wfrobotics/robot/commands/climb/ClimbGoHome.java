package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.command.Command;

public class ClimbGoHome extends Command
{
    private final Climb climb = Climb.getInstance();
    private final double kSpeed;

    public ClimbGoHome()
    {
        this(-0.35);
    }

    public ClimbGoHome(double speed)
    {
        requires(climb);
        kSpeed = speed;
        setTimeout(4.0);
    }

    protected void execute()
    {
        climb.setOpenLoop(kSpeed);
    }

    protected boolean isFinished()
    {
        return climb.hasZeroed() || isTimedOut();
    }

    protected void end()
    {
        climb.setOpenLoop(0.0);
    }
}
