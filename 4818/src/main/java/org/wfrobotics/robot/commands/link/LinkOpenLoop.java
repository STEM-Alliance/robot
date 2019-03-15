package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LinkOpenLoop extends Command
{
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

    protected void init()
    {   
        lastHeightValid = false;
        lastHeight = link.getPosition();
    }

    protected void execute()
    {
        final boolean inAuto = DriverStation.getInstance().isAutonomous();

        if (!inAuto)  // TODO ConditionalCommand cancels requirements
        {
            final double speed = io.getLinkUp() - io.getLinkDown();
            
            
            if(Math.abs(speed) > .05)
            {
                link.setOpenLoop(speed);
                SmartDashboard.putString("Link Open Mode", "open");

                { 
                    holdTimer = Timer.getFPGATimestamp();
                    lastHeightValid = false;
                }
            }
            else if((Timer.getFPGATimestamp() - holdTimer > .75) && !lastHeightValid)
            {
                lastHeight = link.getPosition();
                lastHeightValid = true;
                link.holdAtHeight(lastHeight);
                SmartDashboard.putString("Link Open Mode", "save");
            }
            else if(lastHeightValid) {
                link.holdAtHeight(lastHeight);
                SmartDashboard.putString("Link Open Mode", "hold");
            }
            else
            {
                link.setOpenLoop(speed);
            }

            SmartDashboard.putNumber("Link Hold", lastHeight);
            SmartDashboard.putString("Link Open Mode", "open");
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
