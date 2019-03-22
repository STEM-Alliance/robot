package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class LinkOpenLoop extends Command
{
    private static final double kHoldTimeStart = 0.5;  // seconds
    private static final double kOperatorPresent = 0.05;  // percent output
    private final double kLinkTopDegrees;

    private final Link link = Link.getInstance();
    private final IO io = IO.getInstance();

    private double holdTimer;
    private double lastHeight = 0;
    private boolean lastHeightValid;

    public LinkOpenLoop()
    {
        requires(link);
        kLinkTopDegrees = RobotConfig.getInstance().getLinkConfig().kFullRangeInchesOrDegrees;
    }

    protected void initialize()
    {   
        lastHeightValid = false;
        lastHeight = link.getPosition();
    }

    protected void execute()
    {
        final double linkHeight = link.getPosition();
        double speed = io.getLinkUp() - io.getLinkDown();
        boolean inOpenLoop = true;
        
        if(Math.abs(speed) > kOperatorPresent)
        {
            inOpenLoop = true;

            if ( link.hasZeroed())
            {
                if (linkHeight < 10.0 || linkHeight > kLinkTopDegrees - 10.0)
                {
                    speed /= 3.0;
                }
                else if (linkHeight < 20.0 || linkHeight > kLinkTopDegrees - 20.0)
                {
                    speed /= 2.0;
                }
            }
            
            resetHoldTimer();
        }
        else if((Timer.getFPGATimestamp() - holdTimer > kHoldTimeStart) && !lastHeightValid)
        {
            inOpenLoop = false;

            lastHeight = linkHeight;
            lastHeightValid = true;
        }
        else if(lastHeightValid)
        {
            inOpenLoop = false;
        }

        if (inOpenLoop)
        {
            link.setOpenLoop(speed);
        }
        else
        {
            link.holdAtHeight(lastHeight);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }

    private void resetHoldTimer()
    {
        holdTimer = Timer.getFPGATimestamp();
        lastHeightValid = false;
    }
}
