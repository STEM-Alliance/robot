package frc.robot.subsystems;

import frc.robot.Configuration;
import frc.robot.LoggedNumber;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class IntakeSubsystem extends SubsystemBase {
    private final CANSparkMax m_intake;
    public final DigitalInput m_noteSensor;

    /** Creates a new IntakeSubsystem. */
    public IntakeSubsystem(Robot robot, DigitalInput noteSensor) {
        m_intake = new CANSparkMax(Configuration.kIntakeMotorCanID, MotorType.kBrushless);
        m_intake.setSmartCurrentLimit(Configuration.Neo550Limit);
        m_intake.setIdleMode(IdleMode.kBrake);
        m_noteSensor = noteSensor;
    }

    public void periodic() {
      LoggedNumber.getInstance().logNumber("IntakeSensor", m_noteSensor.get() ? 1.0: 0.0);
      LoggedNumber.getInstance().logNumber("IntakeCurrent", m_intake.getOutputCurrent());
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
            () -> {System.out.println("fwdIntake");},
            () -> m_intake.set(-1),
            interrupted -> m_intake.set(0),
            () -> m_noteSensor.get() && !forceIntake,
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
            () -> m_intake.set(1),
            () -> {},
            interrupted -> m_intake.set(0),
            () -> false,
            this
        );
    }
}
