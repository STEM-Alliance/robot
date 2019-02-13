package org.wfrobotics.robot;


import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData.VisionTargetInfo;
import org.wfrobotics.reuse.subsystems.vision.Point;
import org.wfrobotics.robot.config.ProtoIO;
import org.wfrobotics.robot.config.ProtoRobotConfig;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Preferred provider of global, formatted state about the robot. Commands can get information from one place rather than from multiple subsystems. **/
public final class RobotState extends RobotStateBase
{
    private static final RobotState instance = new RobotState();

    // Robot-specific state
    private double timeSinceRumbleOn;


    public static RobotState getInstance()
    {
        return instance;
    }

    public void reportState()
    {
        super.reportState();

        // TODO Prints all vision based on if we have vision - Flag in VisionProcessor constructor?
        SmartDashboard.putBoolean("Targets In View", visionInView);
        if (visionInView)
        {
            SmartDashboard.putNumber("Vision Error", getVisionError());
        }
    }

    protected synchronized void resetRobotSpecificState()
    {

    }

    /**    |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|
     *     |             Now entering Vision code territory                         |
     *     |   WF robotic is not responsible for anything cased by confusion!       |
     *     |________________________________________________________________________|
     */


    public synchronized void addVisionUpdate(Double time, CoprocessorData coprocessorData)
    {
        visionInView = coprocessorData.targets.size() > 0;
        if (!visionInView)
        {
            visionObservations.clear();
            return;
        }
        VisionTargetInfo largest = getLargestTarget(coprocessorData.targets);
        // TODO circular buffer trim if this gets too big
        visionObservations.add(0, (new Point(time, largest)));
    }

    @Override
    public double getKCameraAngle() {
        return 34.5;
    }
}