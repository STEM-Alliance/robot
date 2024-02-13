package frc.robot.subsystems;

import frc.robot.Configuration;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class ShooterSubsystem extends SubsystemBase {
    private final CANSparkMax m_shooter1;
    private final CANSparkMax m_shooter2;

    private final IntakeSubsystem m_intake;
    private final DigitalInput m_noteSensor;
    private final LimelightSubsystem m_limelight;

    /** Creates a new ShooterSubsystem. */
    public ShooterSubsystem(int[] shooterMotorIDs,
        LimelightSubsystem m_limelightsubsystem, IntakeSubsystem m_intakesubsystem) {
        
        m_intake = m_intakesubsystem;
        m_limelight = m_limelightsubsystem;
        m_noteSensor = m_intake.m_noteSensor;
        
        // Make a CANSparkMax that controls both of the shooter motors
        m_shooter1 = new CANSparkMax(shooterMotorIDs[0], MotorType.kBrushless);
        m_shooter1.setSmartCurrentLimit(Configuration.Neo550Limit);

        m_shooter2 = new CANSparkMax(shooterMotorIDs[1], MotorType.kBrushless);
        m_shooter2.setSmartCurrentLimit(Configuration.Neo550Limit);

        m_shooter2.follow(m_shooter1);
    }

    /* Only if note is in intake, spin up motors, align with limelight and calculate shooting angle
    When motors up to speed and at target angle, shoot note and move to down position */
    public void shootNoteSpeaker() {
        // Make sure that there is a note in the intake
        if (!m_noteSensor.get()) {
            return;
        }

        /* Spin up the shooter motors and angle the shooter while
        waiting for the robot to align to the speaker */
        boolean isShooterReady = spinShooter();
        boolean isShooterAngled = angleShooter(false);
        boolean isRobotAligned = m_limelight.alignToSpeaker();

        // If all the requirements are not met, wait for the next Periodic() loop
        while (!isShooterReady && isShooterAngled && isRobotAligned) {
            // Wait for the next Periodic() loop
            return;
        }

        m_intake.moveToShooter();
    }

    public void shootNoteAmp() {
        // Like the shootNoteSpeaker(), but align and move the shooter to 90? degrees
        return;
    }

    public boolean moveToIntakePos() {
        // Move the shooter / intake to the floor, the intake position
        // Limit switch or motor encoder?
        return true;
    }

    private double calculateShooterAngle(double Distance) {
        /* Example logarithmic curve with values
        {{0.25, 85}, {0.5, 60}, {1, 55}, {2, 35}} (meters, degrees) */
        return 52.5 - 22.3618 * Math.log(Distance);
    }

    private boolean angleShooter(boolean isTargetAmp) {
        if (isTargetAmp) {
            // Drive the motors to the shooters angle to 90? degrees
        }

        double distanceToSpeaker = m_limelight.getDistanceToSpeaker();
        double targetShooterAngle = calculateShooterAngle(distanceToSpeaker);

        // Drive the motors to the shooter to the desired angle
        // Limit switch or motor encoder?
        return true;
    }

    private boolean spinShooter() {
        // Return true when the motors are up to speed, or after timeout (1-2 seconds)
        return true;
    }
}
