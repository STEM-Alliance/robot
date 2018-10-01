package org.wfrobotics.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.math.geometry.Pose2d;
import org.wfrobotics.reuse.math.geometry.Rotation2d;
import org.wfrobotics.reuse.math.geometry.Translation2d;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageTargets;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionTargetInfo;
import org.wfrobotics.robot.GoalTracker.TrackReport;
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


    // --------------------------------------
    // ------------ Vision State ------------
    // --------------------------------------

    // Pose of the camera frame w.r.t. the robot frame
    public static double kCameraXOffset = -3.3211;
    public static double kCameraYOffset = 0.0;
    public static double kCameraZOffset = 20.9;
    public static double kCameraPitchAngleDegrees = 29.56; // Measured on 4/26
    public static double kCameraYawAngleDegrees = 0.0;
    public static double kCameraDeadband = 0.0;
    public static double kBoilerRadius = 7.5;
    public static double kBoilerTargetTopHeight = 88.0;

    private static final Pose2d kVehicleToCamera = new Pose2d(new Translation2d(kCameraXOffset, kCameraYOffset), new Rotation2d());
    private GoalTracker goal_tracker_;
    private Rotation2d camera_pitch_correction_;
    private Rotation2d camera_yaw_correction_;
    private double differential_height_;
    private ShooterAimingParameters cached_shooter_aiming_params_ = null;

    public synchronized void resetVision()
    {
        goal_tracker_ = new GoalTracker();
        camera_pitch_correction_ = Rotation2d.fromDegrees(-kCameraPitchAngleDegrees);
        camera_yaw_correction_ = Rotation2d.fromDegrees(-kCameraYawAngleDegrees);
        differential_height_ = kBoilerTargetTopHeight - kCameraZOffset;
    }

    public synchronized Pose2d getFieldToCamera(double timestamp)
    {
        return getFieldToVehicle(timestamp).transformBy(kVehicleToCamera);
    }

    public synchronized List<Pose2d> getCaptureTimeFieldToGoal()
    {
        List<Pose2d> rv = new ArrayList<>();
        for (TrackReport report : goal_tracker_.getTracks())
        {
            rv.add(Pose2d.fromTranslation(report.field_to_goal));
        }
        return rv;
    }

    public void addVisionUpdate(double timestamp, List<TargetInfo2> vision_update)
    {
        List<Translation2d> field_to_goals = new ArrayList<>();
        Pose2d field_to_camera = getFieldToCamera(timestamp);
        if (!(vision_update == null || vision_update.isEmpty()))
        {
            for (TargetInfo2 target : vision_update)
            {
                double ydeadband = (target.getY() > -kCameraDeadband && target.getY() < kCameraDeadband) ? 0.0 : target.getY();

                // Compensate for camera yaw
                double xyaw = target.getX() * camera_yaw_correction_.cos() + ydeadband * camera_yaw_correction_.sin();
                double yyaw = ydeadband * camera_yaw_correction_.cos() - target.getX() * camera_yaw_correction_.sin();
                double zyaw = target.getZ();

                // Compensate for camera pitch
                double xr = zyaw * camera_pitch_correction_.sin() + xyaw * camera_pitch_correction_.cos();
                double yr = yyaw;
                double zr = zyaw * camera_pitch_correction_.cos() - xyaw * camera_pitch_correction_.sin();

                // find intersection with the goal
                if (zr > 0)
                {
                    double scaling = differential_height_ / zr;
                    double distance = Math.hypot(xr, yr) * scaling + kBoilerRadius;
                    Rotation2d angle = new Rotation2d(xr, yr, true);
                    field_to_goals.add(field_to_camera.transformBy(Pose2d.fromTranslation(new Translation2d(distance * angle.cos(), distance * angle.sin()))).getTranslation());
                }
            }
        }
        synchronized (this)
        {
            goal_tracker_.update(timestamp, field_to_goals);
        }
    }

    public synchronized Optional<ShooterAimingParameters> getCachedAimingParameters()
    {
        return cached_shooter_aiming_params_ == null ? Optional.empty() : Optional.of(cached_shooter_aiming_params_);
    }

    public synchronized Optional<ShooterAimingParameters> getAimingParameters()
    {
        List<TrackReport> reports = goal_tracker_.getTracks();
        if (!reports.isEmpty())
        {
            TrackReport report = reports.get(0);
            Translation2d robot_to_goal = getLatestFieldToVehicle().getValue().getTranslation().inverse().translateBy(report.field_to_goal);
            Rotation2d robot_to_goal_rotation = Rotation2d.fromRadians(Math.atan2(robot_to_goal.y(), robot_to_goal.x()));

            ShooterAimingParameters params = new ShooterAimingParameters(robot_to_goal.norm(), robot_to_goal_rotation, report.latest_timestamp, report.stability);
            cached_shooter_aiming_params_ = params;

            return Optional.of(params);
        }
        return Optional.empty();
    }

    public synchronized void resetVision2()
    {
        goal_tracker_.reset();
        cached_shooter_aiming_params_ = null;
    }

    public void outputToSmartDashboard()
    {
        List<Pose2d> poses = getCaptureTimeFieldToGoal();
        for (Pose2d pose : poses)
        {
            // Only output first goal
            SmartDashboard.putNumber("goal_pose_x", pose.getTranslation().x());
            SmartDashboard.putNumber("goal_pose_y", pose.getTranslation().y());
            break;
        }
        Optional<ShooterAimingParameters> aiming_params = getCachedAimingParameters();
        if (aiming_params.isPresent())
        {
            SmartDashboard.putNumber("goal_range", aiming_params.get().getRange());
            SmartDashboard.putNumber("goal_theta", aiming_params.get().getRobotToGoal().getDegrees());
        }
        else
        {
            SmartDashboard.putNumber("goal_range", 0.0);
            SmartDashboard.putNumber("goal_theta", 0.0);
        }
    }

    /**
     * A container class to specify the shooter angle. It contains the desired range, the field_to_goal_angle
     */
    public class ShooterAimingParameters {
        double range;
        double last_seen_timestamp;
        double stability;
        Rotation2d robot_to_goal;

        public ShooterAimingParameters(double range, Rotation2d robot_to_goal, double last_seen_timestamp,
                                       double stability) {
            this.range = range;
            this.robot_to_goal = robot_to_goal;
            this.last_seen_timestamp = last_seen_timestamp;
            this.stability = stability;
        }

        public double getRange() {
            return range;
        }

        public Rotation2d getRobotToGoal() {
            return robot_to_goal;
        }

        public double getLastSeenTimestamp() {
            return last_seen_timestamp;
        }

        public double getStability() {
            return stability;
        }

    }

    /**
     * A container class for Targets detected by the vision system, containing the location in three-dimensional space.
     */
    public class TargetInfo2 {
        protected double x = 1.0;
        protected double y;
        protected double z;

        public TargetInfo2(double y, double z) {
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}