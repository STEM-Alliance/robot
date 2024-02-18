package frc.robot.subsystems;

import frc.robot.Configuration;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class IntakeSubsystem extends SubsystemBase {
    private final CANSparkMax m_intake;
    private final ShooterSubsystem m_shooter;
    private boolean isIntakeEnabled = false;

    public final DigitalInput m_noteSensor;

    /** Creates a new IntakeSubsystem. */
    public IntakeSubsystem(Robot robot) {
        m_shooter = robot.m_shooter;

        m_noteSensor = new DigitalInput(Configuration.kNoteSensorChannel);

        m_intake = new CANSparkMax(Configuration.kIntakeMotorChannel, MotorType.kBrushless);
        m_intake.setSmartCurrentLimit(Configuration.Neo550Limit);
        m_intake.setIdleMode(IdleMode.kBrake);
    }

    // public void Periodic() {
    //     if (isIntakeEnabled) {
    //         setEnableIntake(!enableIntake());
    //     }
    // }

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

    public void cmdIntake(double cmd)
    {
        SmartDashboard.putBoolean("intake", m_noteSensor.get());
        if (m_noteSensor.get() || cmd > 0) { // If there is not a note or driving outtake
            m_intake.set(cmd);
        } else { // There is a note already in the intake while trying to run intake
            m_intake.set(0);
        }
    }

    public Command fwdIntake(boolean forceIntake) {
        return new FunctionalCommand(
            () -> {},
            () -> m_intake.set(-1),
            interrupted -> m_intake.set(0),
            () -> m_noteSensor.get() && !forceIntake,
            this
        );
    }

    public Command revIntake() {
        return new FunctionalCommand(
            () -> m_intake.set(1),
            () -> {},
            interrupted -> m_intake.set(0),
            () -> false,
            this
        );
    }
}
