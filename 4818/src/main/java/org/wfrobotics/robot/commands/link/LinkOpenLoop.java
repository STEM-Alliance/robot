package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LinkOpenLoop extends Command
{
    private static final double kHoldTimeStart = 0.5;  // seconds
    private static final double kOperatorPresent = 0.05;  // percent output

    //    private final RobotState state = RobotState.getInstance();
    private final Link link = Link.getInstance();
    private final IO io = IO.getInstance();

    private double holdTimer;

    public LinkOpenLoop()
    {
        requires(link);
    }

    private double lastHeight = 0;
    private boolean lastHeightValid;

    protected void initialize()
    {   
        lastHeightValid = false;
        lastHeight = link.getPosition();
    }

    protected void execute()
    {
        final boolean inAuto = DriverStation.getInstance().isAutonomous();

        if (!inAuto)  // TODO ConditionalCommand cancels requirements
        {
            final double linkHeight = link.getPosition();
            double speed = io.getLinkUp() - io.getLinkDown();
            boolean inOpenLoop = true;
            String mode = "open";
            
            if(Math.abs(speed) > kOperatorPresent)
            {
                inOpenLoop = true;

                if (linkHeight < 4.0)
                {
                    // TODO slow down if near top and maybe bottom
                    speed /= 2.0;
                }
                
                resetHoldTimer();
            }
            else if((Timer.getFPGATimestamp() - holdTimer > kHoldTimeStart) && !lastHeightValid)
            {
                inOpenLoop = false;
                mode = "save";

                lastHeight = linkHeight;
                lastHeightValid = true;
            }
            else if(lastHeightValid)
            {
                inOpenLoop = false;
                mode = "hold";
            }

            if (inOpenLoop)
            {
                link.setOpenLoop(speed);
            }
            else
            {
                link.holdAtHeight(lastHeight);
            }

            // SmartDashboard.putString("Link Open Mode", mode);
            // SmartDashboard.putNumber("Link Hold", lastHeight);
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
