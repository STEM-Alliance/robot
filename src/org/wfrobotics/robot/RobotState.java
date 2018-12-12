package org.wfrobotics.robot;


import java.util.ArrayList;
import java.util.List;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData;
import org.wfrobotics.reuse.subsystems.vision.CoprocessorData.VisionTargetInfo;
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
    private double timeSinceRumbleOn;

    // Robot-specific state
    public boolean robotHasCube;

    //vision specific updates
    // ToDo: Move all of these to the RobotStateBase
    public  List<VisionTargetInfo> usableTargets = new ArrayList<VisionTargetInfo>();
    public CoprocessorData latestUpdate;

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

        SmartDashboard.putBoolean("Has VisionServer", (CameraServer.getInstance() != null));
        try {
            SmartDashboard.putString("Message", latestUpdate.toString());
            SmartDashboard.putNumber("vision Error", viaionAngleError);
            SmartDashboard.putBoolean("Target In View", visionInView);
            SmartDashboard.putNumber("Largest Area", usableTargets.get(1).area());
        }
        catch (Exception e) {
        }
    }

    protected synchronized void resetRobotSpecificState()
    {
        robotHasCube = false;
        hasCubeCounts = 0;

        usableTargets.clear();
    }

    public synchronized void addVisionUpdate(CoprocessorData latest)
    {
        latestUpdate = latest;

        if (latest.targets.size() > 0)
        {
            visionInView = true;

            VisionTargetInfo largestTarget = latestUpdate.targets.get(0);
            for (VisionTargetInfo target : latestUpdate.targets)
            {
                if ( target.area() > largestTarget.area() || largestTarget == null)
                {
                    largestTarget = target;
                }
            }
            usableTargets.add(0, largestTarget);
        }
        else {
            visionInView = false;
            viaionAngleError = 0;
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
}