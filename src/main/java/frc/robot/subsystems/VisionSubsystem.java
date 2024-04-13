package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Helpers;

import java.util.List;
import java.util.Optional;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionSubsystem extends SubsystemBase {
    private boolean m_hasTargets = false;
    private double m_fieldHeading = 0.0;
    private double m_visionDataTimestamp = 0.0;
    private Pose2d m_fieldPosition = new Pose2d();

    private PhotonPipelineResult m_cameraResults;
    private double m_currentTimestamp = 0.0;
    private double m_previousTimestamp = 0.0;

    // I have no idea why this works but it does. We double our forward/backward offset
    // and multiply our side to side offset by 1.25, and also dont use rotation?
    // -0.1651 / 2
    private Transform3d m_cameraPose = new Transform3d(new Translation3d(0.1524 * 2, -0.1651 * 1.25, 0.584), new Rotation3d(0, 0, 0));;
    private PhotonCamera m_camera = new PhotonCamera("HQ_Camera");
    private AprilTagFieldLayout m_fieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();
    private PhotonPoseEstimator m_poseEstimator = new PhotonPoseEstimator(
        m_fieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, m_camera, m_cameraPose);

    public VisionSubsystem() {
        m_poseEstimator.setMultiTagFallbackStrategy(PoseStrategy.AVERAGE_BEST_TARGETS);
    }

    public void periodic() {
        try {
            updateVisionData();
        }
        catch (Exception e) {
            System.out.println("Exception while updating vision data.");
            return;
        }
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
     * @return The timestamp of the current vision data.
    */
    public double getVisionDataTimestamp() {
        return m_visionDataTimestamp;
    }

    /**
     * @return The distance to the desired april tag in meters, 0.0 if the tag is not detected.
    */
    public double getTargetDistance(int desiredTargetID) {
        if (m_hasTargets) {
            List<PhotonTrackedTarget> targets = m_cameraResults.getTargets();

            for (PhotonTrackedTarget target : targets) {
                int targetID = target.getFiducialId();

                if (targetID == desiredTargetID) {
                    double targetNorm = target.getBestCameraToTarget().getTranslation().toTranslation2d().getNorm();

                    return Math.abs(targetNorm);
                }
            }
        }

        return 0.0;
    }

    /**
     * @return The angle to the desired april tag in degrees, 0.0 if the tag is not detected.
    */

    public double getTargetYaw(int desiredTargetID) {
        if (m_hasTargets) {
            List<PhotonTrackedTarget> targets = m_cameraResults.getTargets();

            for (PhotonTrackedTarget target : targets) {
                int targetID = target.getFiducialId();

                if (targetID == desiredTargetID) {
                    return -target.getYaw();
                }
            }
        }

        return 0.0;
    }

    private void updateVisionData() {
         m_cameraResults = m_camera.getLatestResult();
        m_currentTimestamp = m_cameraResults.getTimestampSeconds();

        if (isNewDetection()) {
            Optional<EstimatedRobotPose> estimatorResults = m_poseEstimator.update(m_cameraResults);

                if (estimatorResults.isPresent()) {
                    Pose3d estimatedPose = estimatorResults.get().estimatedPose;

                    m_hasTargets = true;
                    m_fieldPosition = new Pose2d(estimatedPose.getTranslation().toTranslation2d(),
                        new Rotation2d(estimatedPose.getRotation().getZ()));
                            
                    m_fieldHeading = m_fieldPosition.getRotation().getDegrees();
            }
        }
        else {
            m_hasTargets = false;
        }

        SmartDashboard.putNumber("Photon X", m_fieldPosition.getX());
        SmartDashboard.putNumber("Photon Y", m_fieldPosition.getY());
        SmartDashboard.putNumber("Photon Heading", m_fieldHeading);

        SmartDashboard.putNumber("Speaker Distance", getTargetDistance(Helpers.getDesiredAprilTag()));
        SmartDashboard.putNumber("Speaker Yaw", getTargetYaw(Helpers.getDesiredAprilTag()));
    }

    private boolean isNewDetection() {
        return m_cameraResults.hasTargets() && Math.abs(m_currentTimestamp - m_previousTimestamp) > 1e-5;
    }
}