package org.wfrobotics.robot.commands.ParellelLink;


import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.robot.subsystems.ParellelLink;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class LinkToHeight extends InstantCommand
{
    private final ParellelLink link = ParellelLink.getInstance();
    private final double angle;

    /**
     * lifts to distance from bottom to top
     * @param degrees 0(bottom) to 1 (top)
     */
    public LinkToHeight(double degrees)
    {
        requires(link);
        angle = degrees;
        if (degrees < -10.0 || degrees > 100.0)
        {
            ConsoleLogger.warning(String.format("Wrist commanded to: %0.0f, range is 0-90 +/-10", angle));
        }
    }

    protected void initialize()
    {
        link.setClosedLoop(angle);
    }
}
