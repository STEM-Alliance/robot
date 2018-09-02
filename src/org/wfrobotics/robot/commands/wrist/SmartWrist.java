package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class SmartWrist extends Command
{
    //    private final RobotState state = RobotState.getInstance();
    private final Wrist wrist = Wrist.getInstance();

    public SmartWrist()
    {
        requires(wrist);
    }

    protected void execute()
    {
        final double commanded = Robot.controls.getWristStick();

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

    protected boolean isFinished()
    {
        return false;
    }
}
