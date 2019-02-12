package org.wfrobotics.robot.commands.link;


import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class LinkToHeight extends InstantCommand
{
    private final Link link = Link.getInstance();
    private final double angle;

    /**
     * lifts to distance from bottom to top
     * @param degrees 0(bottom) to 1 (top)
     */
    public LinkToHeight(double degrees)
    {
        final double kMax = RobotConfig.kLinkTopPosition;

        requires(link);
        angle = degrees;
        if (degrees < -10.0 || degrees > kMax + 10.0)
        {
            String warning = String.format("Link commanded to: %0.0f, range is 0-%.0f +/-10", angle, kMax);
            ConsoleLogger.warning(warning);
        }
    }

    protected void initialize()
    {
        link.setClosedLoop(angle);
    }
}
