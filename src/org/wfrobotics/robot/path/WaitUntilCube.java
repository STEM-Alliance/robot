package org.wfrobotics.robot.path;

import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

public class WaitUntilCube extends Command
{
    private final RobotState state = RobotState.getInstance();

    protected boolean isFinished()
    {
        return state.robotHasCube;
    }
}
