package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.PhotonUtils;

import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionSubsystem extends SubsystemBase {
    private boolean m_hasTargets = false;
    private double m_fieldHeading = 0.0;
    private Pose2d m_fieldPosition = new Pose2d();

    private Transform3d m_cameraPose = new Transform3d();
    private PhotonCamera m_camera = new PhotonCamera("HQ_Camera");
    private AprilTagFieldLayout m_fieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField();

    public VisionSubsystem() {}

    public void periodic() {
        updateVisionData();
    }

    public boolean hasTargets() {
        return m_hasTargets;
    }

    public double getFieldHeading() {
        return m_fieldHeading;
    }

    public Pose2d getFieldPosition() {
        return m_fieldPosition;
    }

    private void updateVisionData() {
        var cameraResults = m_camera.getLatestResult();
        m_hasTargets = cameraResults.hasTargets();

        if (m_hasTargets) {
            var bestTarget = cameraResults.getBestTarget();
            var tagPose = m_fieldLayout.getTagPose(bestTarget.getFiducialId());

            Pose3d robotPose = PhotonUtils.estimateFieldToRobotAprilTag(
                bestTarget.getBestCameraToTarget(), tagPose.get(), m_cameraPose);
            
            // PhotonPoseEstimator photonPoseEstimator = new PhotonPoseEstimator(m_fieldLayout, PoseStrategy.CLOSEST_TO_REFERENCE_POSE, robotToCamera);
            // var robotToCamera = photonPoseEstimator.getRobotToCameraTransform();

            m_fieldPosition = new Pose2d(robotPose.getX(), robotPose.getY(), new Rotation2d(robotPose.getRotation().getZ() + (Math.PI)));
            
            
            SmartDashboard.putNumber("photon x", robotPose.getX());
            SmartDashboard.putNumber("photon y", robotPose.getY());
            SmartDashboard.putNumber("pvRotX", robotPose.getRotation().getX() * 180 / Math.PI);
            SmartDashboard.putNumber("pvRotY", robotPose.getRotation().getY() * 180 / Math.PI);
            SmartDashboard.putNumber("pvRotZ", robotPose.getRotation().getZ() * 180 / Math.PI);
            SmartDashboard.putNumber("photon rotation", m_fieldPosition.getRotation().getDegrees());
        }
    }
}
