package frc.robot.subsystems;

import frc.robot.Robot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LimelightSubsystem extends SubsystemBase {
    private final DrivetrainSubsystem m_swerve;

    private final NetworkTable m_limelight =
        NetworkTableInstance.getDefault().getTable("limelight");

    public LimelightSubsystem(Robot robot) {
        m_swerve = robot.m_swerve;
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
    
}
