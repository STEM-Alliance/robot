package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LimelightSubsystem extends SubsystemBase {
    private final DrivetrainSubsystem m_swerve;

    public LimelightSubsystem(DrivetrainSubsystem m_swervesubsystem) {
        m_swerve = m_swervesubsystem;
    }

    public boolean alignToSpeaker() {
        // Align to the alliances speaker

        return true;
    }

    public double getDistanceToSpeaker() {
        // Get the distance to the alliances speaker in meters
        
        return 0.0;
    }

    public boolean alignToStage() {
        // Align to any side of the alliances stage
        return true;
    }
    
}
