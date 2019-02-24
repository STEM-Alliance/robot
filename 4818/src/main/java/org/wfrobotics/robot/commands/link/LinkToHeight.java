package org.wfrobotics.robot.commands.link;


import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.command.Command;

public class LinkToHeight extends Command
{
    private final Link link = Link.getInstance();
    private final IO io = IO.getInstance();
    private final double desired;

    /**
     * lifts to distance from bottom to top
     * @param degrees 0(bottom) to 1 (top)
     */
    public LinkToHeight(double degrees)
    {
        final double kMax = RobotConfig.getInstance().getLinkConfig().kTicksToTop;

        requires(link);
        desired = degrees;
        if (degrees < 0.0 || degrees > kMax)
        {
            String warning = String.format("Link commanded to: %0.0f, range is 0-%.0f", desired, kMax);
            ConsoleLogger.warning(warning);
        }
    }

    protected void initialize()
    {
        link.setClosedLoop(desired);
    }

    protected boolean isFinished()
    {
        final boolean isClose = Math.abs(link.getPosition() - desired) < 1.0;
        return isClose || io.isElevatorOverrideRequested();
    }

    protected void end()
    {
        link.setOpenLoop(0.0);
    }
}
