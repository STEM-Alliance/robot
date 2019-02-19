package org.wfrobotics.robot;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData;

import edu.wpi.first.wpilibj.DriverStation;

/** Preferred provider of global, formatted state about the robot. Commands can get information from one place rather than from multiple subsystems. **/
public final class RobotState extends RobotStateBase
{
    public static final double kcameraAngle = 34.0;

    private static final RobotState instance = new RobotState();

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

    public void addVisionUpdate(Double time, CoprocessorData coprocessorData)
    {
        DriverStation.reportWarning("RobotState not configured to receive and parse vision updates right now", false);
    }

	@Override
	public double getKCameraAngle() {
		// TODO Auto-generated method stub
		return 34.5;
	}
}