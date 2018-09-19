package org.wfrobotics.robot;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageTargets;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionTargetInfo;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Preferred provider of global, formatted state about the robot. Commands can get information from one place rather than from multiple subsystems. **/
public final class RobotState extends RobotStateBase
{
    // Angle of the camera with used vision error
    private static final double kCamera_Range = 68.5;
    private static final int kHasCubeCountThreshold = 20;
    private final double kIntakeDistanceHasCube;
    private static final double kHasCubeSignalDriveTeamDuration = 2.0;

    private static final RobotState instance = new RobotState();
    private int hasCubeCounts;
    private double timeSinceRumbleOn;

    // Robot-specific state
    public boolean robotHasCube;
    public boolean isLiftMoving;
    public double intakeDistanceToCube;
    public double liftHeightInches;
    public double wristAngleDegrees;

    //vision specific updates
    // ToDo: Move all of these to the RobotStateBase
    public VisionMessageTargets latestUpdate;
    public VisionTargetInfo largestDetected;
    public int centerX;
    public RobotState()
    {
        kIntakeDistanceHasCube = RobotConfig.getInstance().kIntakeDistanceToCube;
    }

    public int centerY;
    // boolean visionInView
    // double visionWidth

    public static RobotState getInstance()
    {
        return instance;
    }

    public void reportState()
    {
        super.reportState();
        SmartDashboard.putBoolean("Has Cube", robotHasCube);
        SmartDashboard.putNumber("Cube", intakeDistanceToCube);
        SmartDashboard.putNumber("Wrist Angle", wristAngleDegrees);

        SmartDashboard.putBoolean("Has VisionServer", (CameraServer.getInstance() != null));
        try {
            SmartDashboard.putString("Message", latestUpdate.msg);
            SmartDashboard.putNumber("vision Error", viaionAngleError);
            SmartDashboard.putBoolean("Target In View", visionInView);
            SmartDashboard.putNumber("Largest Area", largestDetected.area());
        }
        catch (Exception e) {
        }
    }

    protected synchronized void resetRobotSpecificState()
    {
        robotHasCube = false;
        isLiftMoving = false;
        intakeDistanceToCube = 9999.0;  // Big so we don't think we have a cube
        liftHeightInches = 0.0;
        hasCubeCounts = 0;

        largestDetected = null;
    }

    public synchronized void addVisionUpdate(VisionMessageTargets latest)
    {
        latestUpdate = latest;
        centerX = latestUpdate.imageWidth / 2;
        centerY = latestUpdate.imageHeight / 2;

        if (latest.Targets.size() > 0)
        {
            visionInView = true;

            VisionTargetInfo largestTarget = latest.Targets.get(0);
            for (VisionTargetInfo target : latest.Targets)
            {
                if ( target.area() > largestTarget.area() || largestTarget == null)
                {
                    largestTarget = target;
                }
            }
            largestDetected = largestTarget;
            calcVisionError();
        }
        else {
            visionInView = false;
            viaionAngleError = 99999;
        }
    }
    public void calcVisionError()
    {
        double precentError = Double.NaN;
        if (centerX - largestDetected.center_x > 0 ) {
            precentError = 1;
        }
        else if ( centerX - largestDetected.center_x < 0){
            precentError = -1;
        }

        precentError = (centerX - largestDetected.center_x) / centerX;

        viaionAngleError = precentError * (kCamera_Range /2);
    }

    public double getIntakeDistanceCM()  // TODO Use robot standard units (inches)
    {
        return intakeDistanceToCube / 2.54;
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

    public synchronized void updateLift(double inches, boolean isLiftMoving)
    {
        liftHeightInches = inches;
        this.isLiftMoving = isLiftMoving;
    }

    public synchronized void updateWrist(double degrees)
    {
        wristAngleDegrees = degrees;
    }

    private synchronized void handleDetectCube(double cubeDistanceInches)
    {
        intakeDistanceToCube = cubeDistanceInches;

        if (intakeDistanceToCube < kIntakeDistanceHasCube)
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