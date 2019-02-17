package org.wfrobotics.robot;


import java.util.List;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData.VisionTargetInfo;
import org.wfrobotics.reuse.subsystems.vision.Point;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Preferred provider of global, formatted state about the robot. Commands can get information from one place rather than from multiple subsystems. **/
public final class RobotState extends RobotStateBase
{

    private static final RobotState instance = new RobotState();

    // Robot-specific state
    
    private double timeSinceRumbleOn;

    public RobotState()
    {
        
    }

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

    // Robot-specific state

    /**    |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|
     *     |             Now entering Vision code territory                         |
     *     |   WF robotic is not responsible for anything cased by confusion!       |
     *     |________________________________________________________________________|
     */

    public double getKCameraAngle() {return 34.5;}

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