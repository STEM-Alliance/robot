package org.wfrobotics.robot;


import java.util.ArrayList;
import java.util.List;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData.VisionTargetInfo;
import org.wfrobotics.reuse.subsystems.vision.Point;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Preferred provider of global, formatted state about the robot. Commands can get information from one place rather than from multiple subsystems. **/
public final class RobotState extends RobotStateBase
{
    private static final RobotState instance = new RobotState();

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
        SmartDashboard.putNumber("angle", TankSubsystem.getInstance().getGryo());
        SmartDashboard.putNumber("real V", getMeasuredVelocity().dtheta);
        //        try
        //        {
        //            SmartDashboard.putString("Message", update.toString());
        //            SmartDashboard.putNumber("Message", points.size());
        //
        //        }
        //        catch (Exception e)
        //        {
        //        }
        try
        {
            SmartDashboard.putBoolean("Has VisionServer", (CameraServer.getInstance() != null));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SmartDashboard.putBoolean("Target In View", visionInView);
    }

    protected synchronized void resetRobotSpecificState()
    {

    }


    /**    |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|
     *     |             Now entering Vision code territory                         |
     *     |   WF robotic is not responsible for anything cased by confusion!       |
     *     |________________________________________________________________________|
     */

    public final double kcameraAngle = 61.0 / 2.0;

    // Robot-specific state
    public CoprocessorData update;
    public  List<Point> points = new ArrayList<Point>();
    public boolean visionInView = false;

    public void addVisionUpdate(Double time, CoprocessorData coprocessorData)
    {
        SmartDashboard.putString("Ran vision update", "Ran Vision Update");
        update = coprocessorData;

        if (coprocessorData.targets.size() > 0)
        {
            visionInView = true;

            VisionTargetInfo largestTarget = update.targets.get(0);
            for (VisionTargetInfo target : update.targets)
            {
                if ( target.area() > largestTarget.area() || largestTarget == null)
                {
                    largestTarget = target;
                }
            }
            points.add(0, (new Point(time, largestTarget)));
        }
        else {
            visionInView = false;
            //            points.clear();
        }
        if (points.size() > 2)
        {
            SmartDashboard.putNumber("vision Error", getExtrapolatedVisionError());
            SmartDashboard.putNumber("test", points.get(0).getXerror());
            SmartDashboard.putNumber("Y error", points.get(0).getYerror());
        }
        reportState();
    }
    public double getExtrapolatedVisionError()
    {
        return points.get(0).extrapolate(points.get(1), Timer.getFPGATimestamp()).getXerror();
    }
    public double getVisionError()
    {
        return points.get(0).getXerror();
    }
}