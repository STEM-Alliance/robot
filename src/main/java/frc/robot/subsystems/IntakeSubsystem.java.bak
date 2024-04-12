package frc.robot.subsystems;

import frc.robot.Configuration;
import frc.robot.LoggedNumber;
import frc.robot.Robot;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
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
    private int m_wristSetpoint = 0;
    private double m_desiredWristAngle = 0;
    private boolean m_homing = false;
    private boolean m_allowManualIntake = true;

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

        SmartDashboard.putNumber("WristDesiredAngle", m_desiredWristAngle);
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

    public Command fwdIntake(boolean forceIntake) {
        return new FunctionalCommand(
            () -> {System.out.println("fwdIntake"); m_allowManualIntake=false;},
            () -> m_intake.set(-1),
            interrupted -> allowManualIntake(),
            () -> !m_noteSensor.get() || !forceIntake,
            this
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
        m_desiredWristAngle += speed / 50 * 110;
      }

    private void wristControlLoop() {
        if (!m_homing) {
            m_desiredWristAngle = MathUtil.clamp(m_desiredWristAngle, 0, 210 + 2);

            double clampedOut = MathUtil.clamp(m_wristPID.calculate(m_wristEnc.getPosition(),
            m_desiredWristAngle), -1, 1);

            m_wrist.set(clampedOut);
        }

        else {
            m_wrist.set(-0.5);

            m_wristEnc.setPosition(0);
            m_desiredWristAngle = 0;
        }
    }

    public Command startHomeWrist() {
        return new InstantCommand(() -> m_homing = true);
    }

    public Command endHomeWrist() {
        return new InstantCommand(() -> m_homing = false);
    }

    private void moveToSetpoint() {
        m_desiredWristAngle = Configuration.kWristSetpoints[m_wristSetpoint];
    }

    public Command wristSetSetpoint(int setpoint) {
        return new InstantCommand(() -> {m_wristSetpoint = setpoint; moveToSetpoint();});
    }

    public Command wristDelayedSetSetpoint(int setpoint) {
        return new WaitCommand(0.5).andThen(new InstantCommand(() ->
            {m_wristSetpoint = setpoint; moveToSetpoint();}));
    }

    public Command wristAtSetpoint() {
        return new FunctionalCommand(
            () -> {},
            () -> {},
            interrupted -> {},
            () -> Math.abs(m_wristEnc.getPosition() - m_desiredWristAngle) <
            Configuration.kTargetingError
          );
    }
}
