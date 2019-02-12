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
        if (!DriverStation.getInstance().isAutonomous())  // TODO ConditionalCommand cancels requirements
        {
            double speed = io.getLinkUp() - io.getLinkDown();

            if (Math.abs(speed) > 0.1)
            {
                link.setOpenLoop(speed);
            }

            // TODO Need some sort of latched timeout since wrist stick overridden

            //        final boolean liftIsDownWithCube = Math.abs(state.liftHeightInches) < 1 && state.robotHasCube;
            //        final boolean movingFast = Math.abs(state.robotVelocity.getMag()) > 0.33;
            //
            //        if (Math.abs(commanded) < 0.1 && liftIsDownWithCube && movingFast)
            //        {
            //            wrist.setPosition(1.0);
            //        }
            //        else
            //            link.setOpenLoop(commanded);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
