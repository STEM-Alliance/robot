package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.command.Command;

public class LinkToHeight extends Command
{
    private final Link link = Link.getInstance();
    private final IO io = IO.getInstance();
    private final double desired;

    public LinkToHeight(double degrees)
    {
        requires(link);
        setTimeout(3.0);
        desired = degrees;
    }

    protected void initialize()
    {
        link.setClosedLoop(desired);
    }

    protected boolean isFinished()
    {
        final boolean slowEnough = Math.abs(link.getVelocityNative()) < 5.0;
        final boolean isClose = Math.abs(link.getPosition() - desired) < 2.0;
        // SmartDashboard.putBoolean("LinkToHeight IsClose", isClose);
        // SmartDashboard.putBoolean("LinkToHeight slowEnough", isClose);
        // SmartDashboard.putNumber("LinkToHeight IsClose Distance", link.getPosition() - desired);
        return (isClose && slowEnough) || io.isLinkOverrideRequested() || isTimedOut();
    }
}
