package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class LinkOpenLoop extends Command
{
    //    private final RobotState state = RobotState.getInstance();
    private final Link link = Link.getInstance();
    private final IO io = IO.getInstance();

    public LinkOpenLoop()
    {
        requires(link);
    }

    protected void execute()
    {
        final boolean inAuto = DriverStation.getInstance().isAutonomous();

        if (!inAuto)  // TODO ConditionalCommand cancels requirements
        {
            final double speed = io.getLinkUp() - io.getLinkDown();

            link.setOpenLoop(speed);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
