package org.wfrobotics.robot;

import org.wfrobotics.reuse.RobotStateBase;


/** Provider of global, formatted state about the robot. Consider calling subsystem instance instead. **/
public final class RobotState extends RobotStateBase
{
    public static RobotState getInstance()
    {
        return instance;
    }

    private static final RobotState instance = new RobotState();

    protected synchronized void resetRobotSpecificState()
    {

    }
}