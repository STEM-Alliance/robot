package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class WristToHeight extends Command
{
    private final Wrist wrist = Wrist.getInstance();
    protected final double desired;

    public WristToHeight(double desired)
    {
        requires(wrist);
        this.desired = desired;
    }

    protected void initialize()
    {
        wrist.setClosedLoop(desired);
    }

    protected boolean isFinished()
    {
        final boolean isClose = Math.abs(wrist.getPosition() - desired) < 1.0;
        return isClose;
    }

    protected void end()
    {
        wrist.setOpenLoop(0.0);  // In autonomous, this holds the current position
    }
}
