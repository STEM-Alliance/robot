package frc.robot.subsystems;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configuration;

public class ClimberSubsystem extends SubsystemBase {
    private final DigitalInput m_stageSensor;
    private final ShooterSubsystem m_shooter;
    private final DrivetrainSubsystem m_swerve;
    private final LimelightSubsystem m_limelight;

    /** Creates a new ClimberSubsystem. */
    public ClimberSubsystem(LimelightSubsystem m_limelightsubsystem,
        ShooterSubsystem m_shootersubsystem, DrivetrainSubsystem m_swervesubsystem) {
        
        m_limelight = m_limelightsubsystem;
        m_shooter = m_shootersubsystem;
        m_swerve = m_swervesubsystem;

        m_stageSensor = new DigitalInput(Configuration.kStageSensorChannel);
    }

    public void climbChain() {
        boolean isAligned = m_limelight.alignToStage();

        while (!isAligned) {
            return;
        }

        m_shooter.moveToIntakePos();

        /* While facing the stage, drive forward until the stage sensor detects
        that the robot is in position under the stage */
        while (!m_stageSensor.get())
        m_swerve.driveRobotSpeeds(new ChassisSpeeds(
            Configuration.kClimberAligningSpeed, 0, 0));

        deployClimber();
    }

    private void deployClimber() {
        // Once in position under the stage, back out and deploy the climber
        // Once fully deployed and on the chain, raise the climber
        // Score in the trap?
        return;
    }
    
}