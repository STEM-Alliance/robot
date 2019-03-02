package org.wfrobotics.robot;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData;

/** Preferred provider of global, formatted state about the robot. Commands can get information from one place rather than from multiple subsystems. **/
public final class RobotState extends RobotStateBase
{
    private static final RobotState instance = new RobotState();
    public static double kcameraAngle = 0;

    // Robot-specific state

    public static RobotState getInstance()
    {
        return instance;
    }

    public void reportState()
    {
        super.reportState();
    }

    protected synchronized void resetRobotSpecificState()
    {

    }



	@Override
	public void addVisionUpdate(Double time, CoprocessorData coprocessorData) {
		// TODO Auto-generated method stub
		
	}
}