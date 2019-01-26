package org.wfrobotics.robot.commands.ParellelLink;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.ParellelLink;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class SmartLink extends Command
{
    //    private final RobotState state = RobotState.getInstance();
    private final ParellelLink wrist = ParellelLink.getInstance();
    private final IO io = IO.getInstance();

    public SmartLink()
    {
        requires(wrist);
    }

    protected void execute()
    {
        if (!DriverStation.getInstance().isAutonomous())  // TODO ConditionalCommand cancels requirements
        {
            final double commanded = io.getWristStick();

            // TODO Need some sort of latched timeout since wrist stick overridden

            //        final boolean liftIsDownWithCube = Math.abs(state.liftHeightInches) < 1 && state.robotHasCube;
            //        final boolean movingFast = Math.abs(state.robotVelocity.getMag()) > 0.33;
            //
            //        if (Math.abs(commanded) < 0.1 && liftIsDownWithCube && movingFast)
            //        {
            //            wrist.setPosition(1.0);
            //        }
            //        else
            {
                wrist.setOpenLoop(commanded);
            }
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
