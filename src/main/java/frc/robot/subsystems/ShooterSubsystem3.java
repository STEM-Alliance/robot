package frc.robot.subsystems;

import frc.robot.Configuration;
import frc.robot.LoggedNumber;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class ShooterSubsystem3 extends SubsystemBase {
    private final CANSparkMax m_shooter;
    private final CANSparkMax m_shooter2;
    private final CANSparkMax m_arm;
    private final RelativeEncoder m_armEnc;
    private final RelativeEncoder m_shooterEnc;
    private final DutyCycleEncoder m_absEncoder;
    
    private final ProfiledPIDController m_armPID = new ProfiledPIDController(
        Configuration.kShooterArmKp, Configuration.kShooterArmKi, Configuration.kShooterArmKd,
        new TrapezoidProfile.Constraints(
            Configuration.kShooterArmMaxSpeed, Configuration.kShooterArmAcceleration));

    private static int[] m_shooterMotorChannels = Configuration.kShooterMotorCanID;
    private static int m_shooterArmMotorChannel = Configuration.kShooterArmMotorCanID;

    // Give setpoints their own variable so commands are simpler
    private static double m_unhookPosition = Configuration.kShooterArmUnhookPosition;
    private static double m_autoShootingPosition = Configuration.kShooterArmLoweredPosition;

    private static double m_ampPosition = Configuration.kShooterArmSetpoints[2];

    private static int m_setpoints = Configuration.kShooterArmSetpoints.length - 1;

    private double m_desiredAngle = 0;
    private int m_angleSetpoint = 0;

    public ShooterSubsystem3() {
        // Make a CANSparkMax that controls both of the shooter motors
        m_shooter = new CANSparkMax(m_shooterMotorChannels[0], MotorType.kBrushless);
        m_shooter2 = new CANSparkMax(m_shooterMotorChannels[1], MotorType.kBrushless);

        m_shooter.setSmartCurrentLimit(Configuration.NeoLimit);
        m_shooter2.setSmartCurrentLimit(Configuration.NeoLimit);

        m_shooter2.follow(m_shooter);
        m_shooterEnc = m_shooter.getEncoder();

        // Make a CANSparkMax that controls the arm
        m_arm = new CANSparkMax(m_shooterArmMotorChannel, MotorType.kBrushless);

        m_arm.setSmartCurrentLimit(Configuration.NeoLimit);

        m_armEnc = m_arm.getEncoder();
        m_armEnc.setPositionConversionFactor(Configuration.kShooterArmRatio);
        m_armEnc.setPosition(0);

        m_absEncoder = new DutyCycleEncoder(Configuration.kShooterArmEncoderChannel);
        m_absEncoder.setDistancePerRotation(360);
        m_absEncoder.reset();

        // Only allow integral for the arm PID when the error is less than +-5 degrees
        m_armPID.setIZone(5);
    }

    public void periodic() {
        syncArmEncoders();
        
        armControlLoop();
    }

    private void armControlLoop() {
        // Software limits for the desired angle and the PID
        m_desiredAngle = MathUtil.clamp(m_desiredAngle,
            Configuration.kShooterArmLowerLimit, Configuration.kShooterArmUpperLimit);

        m_armPID.setGoal(m_desiredAngle);

        double armOutput = m_armPID.calculate(getArmPos());
        armOutput = MathUtil.clamp(armOutput,
            -Configuration.kShooterArmPIDLimit, Configuration.kShooterArmPIDLimit);

        m_arm.set(armOutput);

        // Logging
        SmartDashboard.putNumber("m_desiredAngle", m_desiredAngle);
        SmartDashboard.putNumber("currentAngle", getArmPos());

        LoggedNumber.getInstance().logNumber("DesiredAngle", m_desiredAngle);
        LoggedNumber.getInstance().logNumber("CurrentAngle", getArmPos());
        LoggedNumber.getInstance().logNumber("ArmPIDOut", armOutput);
        LoggedNumber.getInstance().logNumber("ArmCurrent", m_arm.getOutputCurrent());
    }

    private double getArmPos() {
        double armPos = 0;

        if (m_absEncoder.isConnected()) {
            armPos = m_absEncoder.getDistance();
        }
        else {
            armPos = m_armEnc.getPosition();
        }

        LoggedNumber.getInstance().logNumber("ArmEncoderConnected",
            m_absEncoder.isConnected() ? 1.0: 0.0);

        return armPos;
    }

    private void syncArmEncoders() {
        if (m_absEncoder.isConnected()) {
            double absEncoderPos = m_absEncoder.getDistance();
            double error = Math.abs(m_armEnc.getPosition() - absEncoderPos);

            // If there is a large error, sync the arm encoder to the absolute encoder
            // If the absolute encoder gets disconnected, it will switch over to the arm encoder
            if (error > 2.5) {
                m_armEnc.setPosition(absEncoderPos);
            }
        }
    }

    // Spin the shooter motor and return true when the velocity is above the minimum
    public Command spinShooterToVelocity() {
        return new FunctionalCommand(
            () -> m_shooter.set(-1),
            () -> {},
            interrupted -> {},
            () -> Math.abs(m_shooterEnc.getVelocity()) >= Configuration.kMinFlywheelSpeed,
            this
        );
    }

    // Spin the shooter motor with a speed and return true when the velocity is above the minimum
    public Command spinShooterToVelocity(double speed) {
        return new FunctionalCommand(
            () -> m_shooter.set(speed),
            () -> {},
            interrupted -> {},
            () -> Math.abs(m_shooterEnc.getVelocity()) >= 15,
            this
        );
    }

    // Stop the shooter
    public Command stopShooter() {
        return new InstantCommand(() -> m_shooter.set(0));
    }

    // Auto command, move the shooter up to release the pin
    public Command unhookShooter() {
        return new FunctionalCommand(
            () -> {m_desiredAngle = m_unhookPosition;},
            () -> {},
            interrupted -> {},
            () -> Math.abs(getArmPos() - m_unhookPosition) <
            Configuration.kTargetingError,
            this
        );
    }

    // Auto command, move the shooter to the shooting position
    public Command lowerShooter() {
        return new FunctionalCommand(
            () -> {m_desiredAngle = m_autoShootingPosition;},
            () -> {},
            interrupted -> {},
            () -> Math.abs(getArmPos() - m_autoShootingPosition) <
                Configuration.kTargetingError,
            this
        );
    }

    // Move the shooter to the amp position
    public Command ampPosition() {
        return new FunctionalCommand(
            () -> {m_desiredAngle = m_ampPosition;},
            () -> {},
            interrupted -> {},
            () -> Math.abs(getArmPos() - m_ampPosition) < Configuration.kTargetingError,
            this
        );
    }

    // Returns false until the arm is at the desired angle, doesnt require the subsystem
    public Command atSetpoint() {
        return new FunctionalCommand(
            () -> {},
            () -> {},
            interrupted -> {},
            () -> Math.abs(getArmPos() - m_desiredAngle) < Configuration.kTargetingError
        );
    }

    public void setPosition(double speed) {
        m_desiredAngle += speed / 50 * Configuration.kShooterArmChangeRate;
    }

    public void moveToSetpoint() {
        m_angleSetpoint = MathUtil.clamp(m_angleSetpoint, 0, m_setpoints);

        m_desiredAngle = Configuration.kShooterArmSetpoints[m_angleSetpoint];
    }

    public void moveSetpointUp() {
        m_angleSetpoint++;
        moveToSetpoint();
    }

    public void moveSetpointDown() {
        m_angleSetpoint--;
        moveToSetpoint();
    }

    public Command setSetpoint(int setpoint) {
        return new InstantCommand(() -> {m_angleSetpoint = setpoint; moveToSetpoint();});
    }
}