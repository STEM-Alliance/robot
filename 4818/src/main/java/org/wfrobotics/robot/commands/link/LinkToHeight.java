package org.wfrobotics.robot.commands.link;


import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
        setTimeout(3);
        desired = degrees;
        if (degrees < 0.0 || degrees > kMax)
        {
            // String warning = String.format("Link commanded to: %0.0f, range is 0-%.0f", desired, kMax);
            // ConsoleLogger.warning(warning);
        }
    }

    protected void initialize()
    {
        link.setClosedLoop(desired);
    }

    protected boolean isFinished()
    {
        final boolean slowEnough = Math.abs(link.getVelocityNative()) < 5.0;
        final boolean isClose = Math.abs(link.getPosition() - desired) < 2.0;
        SmartDashboard.putBoolean("LinkToHeight IsClose", isClose);
        SmartDashboard.putBoolean("LinkToHeight slowEnough", isClose);
        SmartDashboard.putNumber("LinkToHeight IsClose Distance", link.getPosition() - desired);
        return (isClose && slowEnough) || io.isLinkOverrideRequested() || isTimedOut();
    }

    protected void end()
    {
        link.setOpenLoop(0.0);
    }
}
