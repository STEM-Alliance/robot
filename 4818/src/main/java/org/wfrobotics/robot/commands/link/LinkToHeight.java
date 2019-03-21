package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.command.Command;

public class LinkToHeight extends Command
{
    private final double kOnTargetDegrees;

    private final Link link = Link.getInstance();
    private final IO io = IO.getInstance();
    private final double desired;

    public LinkToHeight(double degrees)
    {
        requires(link);

        final RobotConfig config = RobotConfig.getInstance();

        kOnTargetDegrees = config.kLinkOnTargetDegrees;
        desired = degrees;
        setTimeout(3.0);
    }

    protected void initialize()
    {
        link.setClosedLoop(desired);
    }

    protected boolean isFinished()
    {
        final boolean slowEnough = link.onTarget();
        final boolean isClose = Math.abs(link.getPosition() - desired) < kOnTargetDegrees;
        return (isClose && slowEnough) || io.isLinkOverrideRequested() || isTimedOut();
    }
}
