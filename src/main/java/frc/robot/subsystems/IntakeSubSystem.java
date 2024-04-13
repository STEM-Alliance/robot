package frc.robot.subsystems;

import frc.robot.Configuration;
import frc.robot.LoggedNumber;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class IntakeSubsystem extends SubsystemBase {
    private final CANSparkMax m_intake;
    private final CANSparkMax m_wrist;
    public final DigitalInput m_noteSensor;
    private final RelativeEncoder m_wristEnc;

    private final PIDController m_wristPID = new PIDController(0.05, 0, 0);
    private double m_wristDesiredAngle = 0;
    private boolean m_homing = false;
    private boolean m_allowManualIntake = true;

    private Debouncer m_debouncer = new Debouncer(0.25, Debouncer.DebounceType.kBoth);

    /** Creates a new IntakeSubsystem. */
    public IntakeSubsystem(DigitalInput noteSensor) {
      m_intake = new CANSparkMax(Configuration.kIntakeMotorCanID, MotorType.kBrushless);
      m_intake.setSmartCurrentLimit(Configuration.Neo550Limit);
      m_intake.setIdleMode(IdleMode.kBrake);
      m_wrist = new CANSparkMax(Configuration.kWristMotorCanID, MotorType.kBrushless);
      m_wrist.setSmartCurrentLimit(Configuration.NeoLimit);
      m_wrist.setIdleMode(IdleMode.kBrake);
      m_wristEnc = m_wrist.getEncoder();
      m_wristEnc.setPosition(0);
      m_noteSensor = noteSensor;
    }

    public void periodic() {
        wristControlLoop();
        LoggedNumber.getInstance().logNumber("IntakeSensor", m_noteSensor.get() ? 1.0: 0.0);
        LoggedNumber.getInstance().logNumber("IntakeCurrent", m_intake.getOutputCurrent());
        LoggedNumber.getInstance().logNumber("WristPosition", m_wristEnc.getPosition());
        SmartDashboard.putNumber("WristPosition", m_wristEnc.getPosition());

        SmartDashboard.putNumber("WristDesiredAngle", m_wristDesiredAngle);
    }

    public void cmdIntake(double cmd)
    {
        SmartDashboard.putBoolean("intake", m_noteSensor.get());
        if (m_allowManualIntake) {
            //System.out.println("Sensor: " + m_noteSensor.get() + " cmd: " + cmd);
            if (m_noteSensor.get() || cmd > 0) { // If there is not a note or driving outtake
                m_intake.set(cmd);
            } else { // There is a note already in the intake while trying to run intake
                m_intake.set(0);
            }
        }
    }

    private void allowManualIntake() {
        m_intake.set(0);
        m_allowManualIntake = true;
    }


    // We want to run this if the note sensor is true or force is true
    // 0 0 1 // 1 1 1
    // 0 1 0 // 1 0 0
    // 1 0 0 // 0 1 0
    // 1 1 0 // 0 0 0
    // 
    public Command fwdIntake(boolean forceIntake) {
        return new FunctionalCommand(
            () -> {System.out.println("fwdIntake"); m_allowManualIntake=false;},
            () -> m_intake.set(-1),
            interrupted -> allowManualIntake(),
            () -> !m_noteSensor.get() && !forceIntake,
            this
        );
    }

    public Command noNote() {
        return new FunctionalCommand(
            () -> {},
            () -> {},
            interrupted -> {},
            () -> m_debouncer.calculate(m_noteSensor.get())
        );
    }

    public Command fwdIntakeTimed() {
        return new InstantCommand(() -> m_intake.set(-1));
    }

    public Command stopIntake() {
        return new InstantCommand(() -> m_intake.set(0));
    }

    public Command revIntake() {
        return new FunctionalCommand(
            () -> {m_intake.set(1); m_allowManualIntake=false;},
            () -> {},
            interrupted -> allowManualIntake(),
            () -> false,
            this
        );
    }

    public Command cmdWrist(double speed) {
        return new FunctionalCommand(
            () -> m_wrist.set(speed),
            () -> {},
            interrupted -> m_wrist.set(0),
            () -> false,
            this
        );
    }

    public void setPosition(double speed) {
        m_wristDesiredAngle += speed / 50 * 110;
      }

    private void wristControlLoop() {
        if (!m_homing) {
            m_wristDesiredAngle = MathUtil.clamp(m_wristDesiredAngle, 0, Configuration.kWristLimit + Configuration.kWristLimitError);

            double clampedOut = MathUtil.clamp(m_wristPID.calculate(m_wristEnc.getPosition(),
            m_wristDesiredAngle), -1, 1);

            m_wrist.set(clampedOut);
        }

        else {
            m_wrist.set(-0.5);

            m_wristEnc.setPosition(0);
            m_wristDesiredAngle = 0;
        }
    }

    public Command wristStartHome() {
        return new InstantCommand(() -> m_homing = true);
    }

    public Command wristEndHome() {
        return new InstantCommand(() -> m_homing = false);
    }

    public Command wristSetSetpoint(Configuration.kWristSetpoints setpoint) {
        return new InstantCommand(() -> {m_wristDesiredAngle = setpoint.getRotationTarget();});
    }

    public Command wristAtSetpoint() {
        return new FunctionalCommand(
            () -> {},
            () -> {},
            interrupted -> {},
            () -> Math.abs(m_wristEnc.getPosition() - m_wristDesiredAngle) <
            Configuration.kTargetingError
          );
    }
}
