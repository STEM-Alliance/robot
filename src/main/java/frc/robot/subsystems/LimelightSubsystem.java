package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.Configuration;
import frc.robot.LimelightHelpers;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LimelightSubsystem extends SubsystemBase {
    private final DrivetrainSubsystem m_swerve;

    public LimelightSubsystem(DrivetrainSubsystem swerve) {
        m_swerve = swerve;
    }

    public boolean alignToSpeaker() {
        // Align to the alliances speaker
        return true;
    }

    public double getDistanceToSpeaker() {
        // Get the distance to the alliances speaker in meters
        
        return 0.0;
    }

    public boolean alignToAmp() {
        // Align to the alliances amp

        return true;
    }

    public boolean alignToStage() {
        // Align to any side of the alliances stage
        return true;
    }

    // https://docs.wpilib.org/en/stable/docs/software/advanced-controls/state-space/state-space-pose-estimators.html
    // run once in init, dont add to odometry
    public void updatePose(boolean addToOdometry) {
        var alliance = DriverStation.getAlliance();

        if (alliance.isPresent() && LimelightHelpers.getTV(Configuration.kLimelightName)) {
            double[] botPoseBlue = LimelightHelpers.getBotPose_wpiBlue(
                Configuration.kLimelightName);
                
            double botHeading = botPoseBlue[5];

            if (alliance.get() == Alliance.Red) {
                // Flip the heading if the alliance is red
                botHeading = (botHeading - 180) % 360; // ?
            }

            // botPose X, Y, and adjusted heading
            Pose2d botPose = new Pose2d(new Translation2d(botPoseBlue[0], botPoseBlue[1]),
                new Rotation2d(botHeading));

            if (addToOdometry) {
                // m_swerve.addVisionMeasurement(botPose,
                //     Timer.getFPGATimestamp() - botPoseBlue[6] / 1000);

            } else {
                m_swerve.resetPose(botPose);
            }
        }
    }
}