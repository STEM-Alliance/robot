package org.wfrobotics.robot;


import java.util.ArrayList;
import java.util.List;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData.VisionTargetInfo;
import org.wfrobotics.reuse.subsystems.vision.Point;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Preferred provider of global, formatted state about the robot. Commands can get information from one place rather than from multiple subsystems. **/
public final class RobotState extends RobotStateBase
{
    private static final int kHasCubeCountThreshold = 20;
    private final double kIntakeDistanceHasCube;
    private static final double kHasCubeSignalDriveTeamDuration = 2.0;

    private static final RobotState instance = new RobotState();
    private int hasCubeCounts;

    // Robot-specific state
    public boolean robotHasCube;
    private double timeSinceRumbleOn;

    public RobotState()
    {
        kIntakeDistanceHasCube = RobotConfig.getInstance().kIntakeDistanceToCube;
    }

    public static RobotState getInstance()
    {
        return instance;
    }

    public void reportState()
    {
        super.reportState();
        SmartDashboard.putBoolean("Has Cube", robotHasCube);
        try
        {
            SmartDashboard.putBoolean("Has VisionServer", (CameraServer.getInstance() != null));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SmartDashboard.putBoolean("Vision Targets", visionInView);
        if (visionPoints.size() > 2)
        {
            SmartDashboard.putNumber("Vision Error", getVisionError());
            SmartDashboard.putNumber("Vision Extrapolated", getExtrapolatedVisionError());
            SmartDashboard.putNumber("test", visionPoints.get(0).getXerror());
        }
    }

    public void updateIntake(double distance)
    {
        handleDetectCube(distance);

        if(robotHasCube)
        {
            final double now = Timer.getFPGATimestamp();

            IO.getInstance().setRumble(now - timeSinceRumbleOn < 1.0);
            if (now - timeSinceRumbleOn < kHasCubeSignalDriveTeamDuration)
            {
                Robot.leds.signalDriveTeam();  // TODO latched boolean?
            }
            else
            {
                Robot.leds.useRobotModeColor();
                timeSinceRumbleOn = 0.0;
            }
        }
        else
        {
            IO.getInstance().setRumble(false);
        }
    }

    protected synchronized void resetRobotSpecificState()
    {
        robotHasCube = false;
        hasCubeCounts = 0;
    }

    private synchronized void handleDetectCube(double cubeDistanceInches)
    {
        if (cubeDistanceInches < kIntakeDistanceHasCube)
        {
            hasCubeCounts++;
        }
        else if (hasCubeCounts < kHasCubeCountThreshold)
        {
            timeSinceRumbleOn = Timer.getFPGATimestamp();
        }
        else
        {
            hasCubeCounts = 0;
        }
        robotHasCube = hasCubeCounts > kHasCubeCountThreshold;
    }


    /**    |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|
     *     |             Now entering Vision code territory                         |
     *     |   WF robotic is not responsible for anything cased by confusion!       |
     *     |________________________________________________________________________|
     */

    public final double kcameraAngle = 34.5;

    public List<Point> visionPoints = new ArrayList<Point>();
    public boolean visionInView = false;

    public void addVisionUpdate(Double time, CoprocessorData coprocessorData)
    {
        if (coprocessorData.targets.size() == 0)
        {
            visionInView = false;
            visionPoints.clear();
            return;
        }

        visionInView = true;

        VisionTargetInfo largestTarget = coprocessorData.targets.get(0);
        for (VisionTargetInfo target : coprocessorData.targets)
        {
            if ( target.area() > largestTarget.area() || largestTarget == null)
            {
                largestTarget = target;
            }
        }
        visionPoints.add(0, (new Point(time, largestTarget)));
    }

    public double getExtrapolatedVisionError()
    {
        if (visionPoints.size() < 2)
        {
            return 0;
        }

        final Point errorBefore = visionPoints.get(1);
        final Point errorCurrent = visionPoints.get(0);
        final double now = Timer.getFPGATimestamp();
        return errorCurrent.extrapolate(errorBefore, now).getXerror();
    }

    public double getVisionError()
    {
        if (visionPoints.size() == 0)
        {
            return 0;
        }
        return visionPoints.get(0).getXerror();
    }
}