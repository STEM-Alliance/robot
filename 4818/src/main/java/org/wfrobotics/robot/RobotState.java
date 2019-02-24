package org.wfrobotics.robot;


import java.util.List;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData.VisionTargetInfo;
import org.wfrobotics.reuse.subsystems.vision.Point;

/** Provider of global, formatted state about the robot. Consider calling subsystem instance instead. **/
public final class RobotState extends RobotStateBase
{
    public static RobotState getInstance()
    {
        return instance;
    }

    private static final RobotState instance = new RobotState();

    public RobotState()
    {
        super();
    }

    public void reportState()
    {
        super.reportState();
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
        visionInView = coprocessorData.targets.size() >= 2;
        if (!visionInView)
        {
            visionObservations.clear();
            return;
        }
        double center = getCenterOfTwoClosest(coprocessorData.targets);
        // TODO circular buffer trim if this gets too big
        if (visionObservations.size() > 5)
        {
            visionObservations.remove(4);
        }
        visionObservations.add(0, (new Point(time, center, 0.0)));
    }

    private double getCenterOfTwoClosest(List<VisionTargetInfo> targets)
    {
        double errorSmallest = Double.MAX_VALUE;
        double errorNextSmallest = Double.MAX_VALUE;

        for (VisionTargetInfo target : targets)
        {
            double error = target.getX();
            if (Math.abs(error) < Math.abs(errorSmallest))
            {
                errorNextSmallest = errorSmallest;  // Shift them down by one
                errorSmallest = error;
            }
            else if (Math.abs(error) < Math.abs(errorNextSmallest))
            {
                errorNextSmallest = error;
            }
        }
        return (errorSmallest + errorNextSmallest) / 2.0;
    }
}