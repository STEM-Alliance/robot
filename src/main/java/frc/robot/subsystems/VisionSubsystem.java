package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;

import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionSubsystem extends SubsystemBase {
    private boolean m_hasTargets = false;
    private double m_fieldHeading = 0.0;
    private double m_visionDataLatency = 0.0;
    private Pose2d m_fieldPosition = new Pose2d();

    private Transform3d m_cameraPose = new Transform3d();
    private PhotonCamera m_camera = new PhotonCamera("HQ_Camera");
    private AprilTagFieldLayout m_fieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();

    public VisionSubsystem() {}

    public void periodic() {
        updateVisionData();
    }

    /**
     * @return True if there are any tags detected by the vision subsystem, false if not.
    */
    public boolean hasTargets() {
        return m_hasTargets;
    }

    /**
     * @return The robots heading relative to the field.
    */
    public double getFieldHeading() {
        return m_fieldHeading;
    }

    /**
     * @return A Pose2d with the field X, Y, and heading, in meters and degrees -180 to 180.
    */
    public Pose2d getFieldPosition() {
        return m_fieldPosition;
    }

    /**
     * @return The latency of the vision pipeline in seconds.
    */
    public double getVisionDataLatency() {
        return m_visionDataLatency;
    }

    /**
     * @return The FPGA timestamp minus the vision data latency, the timestamp of the vision data on the FPGA in seconds.
    */
    public double getVisionDataTimestamp() {
        return Timer.getFPGATimestamp() - m_visionDataLatency;
    }

    private void updateVisionData() {
        var cameraResults = m_camera.getLatestResult();
        m_hasTargets = cameraResults.hasTargets();

        if (m_hasTargets) {
            var bestTarget = cameraResults.getBestTarget();
            var tagPose = m_fieldLayout.getTagPose(bestTarget.getFiducialId());
            Pose3d robotPose = PhotonUtils.estimateFieldToRobotAprilTag(
                bestTarget.getBestCameraToTarget(), tagPose.get(), m_cameraPose);

            m_visionDataLatency = cameraResults.getLatencyMillis() / 1000;
            // 0 faces red, 180 faces blue, rotation given in radians?, rotation2d takes value of radians
            m_fieldPosition = new Pose2d(robotPose.getX(), robotPose.getY(), new Rotation2d(-(robotPose.getRotation().getZ() - Math.PI)));
            
            SmartDashboard.putNumber("photon x", robotPose.getX());
            SmartDashboard.putNumber("photon y", robotPose.getY());
            SmartDashboard.putNumber("pvRotZ", -(robotPose.getRotation().getZ() * 180 / Math.PI));
        }
    }
}
