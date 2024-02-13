package frc.robot.subsystems;

import frc.robot.Configuration;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class IntakeSubsystem extends SubsystemBase {
    private final CANSparkMax m_intake;
    private final ShooterSubsystem m_shooter;

    final DigitalInput m_noteSensor = new DigitalInput(Configuration.kNoteSensorChannel);

    private boolean isIntakeEnabled = false;

    /** Creates a new IntakeSubsystem. */
    public IntakeSubsystem(int intakeMotorID, ShooterSubsystem m_shootersubsystem,
        DigitalInput noteSensorID) {
        m_shooter = m_shootersubsystem;

        m_intake = new CANSparkMax(intakeMotorID, MotorType.kBrushless);
        m_intake.setSmartCurrentLimit(Configuration.Neo550Limit);
    }

    public void Periodic() {
        if (isIntakeEnabled) {
            setEnableIntake(!enableIntake());
        }
    }

    // Make sure intake is in position, no note is in the intake, drive intake motors
    // Once light sensor detects note, make sure the note is fully in the intake and stop it
    private boolean enableIntake() {
        boolean inIntakePosition = m_shooter.moveToIntakePos();

        if (!inIntakePosition) {
            return false;
        }

        return intakeNote();
    }

    private boolean intakeNote() {
        // Drive intake motor until note sensor detected

        if (m_noteSensor.get()) {
            /* Drive the motor for another 0.5 seconds to make
            sure the note is fully in the intake */

            return true;
        }

        return false;
    }

    public void moveToShooter() {
        // Feed the note into the shooter flywheels
        return;
    }

    public void setEnableIntake(boolean set) {
        isIntakeEnabled = set;
    }
}
