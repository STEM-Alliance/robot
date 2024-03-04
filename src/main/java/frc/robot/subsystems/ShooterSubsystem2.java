package frc.robot.subsystems;

import frc.robot.Configuration;
import frc.robot.Robot;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;
import frc.robot.commands.*;

public class ShooterSubsystem2 extends SubsystemBase {
    private final CANSparkMax m_shooter;
    private final CANSparkMax m_shooter2;
    private final CANSparkMax m_armMotor;
    private final RelativeEncoder m_armEnc;
    private final RelativeEncoder m_shooterEnc;

    private final DigitalInput m_lowLimitSwitch = new DigitalInput(Configuration.kLowArmLimitswitchChannel);
    private final DigitalInput m_upperLimitSwitch = new DigitalInput(Configuration.kUpperArmLimitswitchChannel);
    
    private final PIDController m_shooterPID = new PIDController(Configuration.kShooterArmKp,
    Configuration.kShooterArmKi, Configuration.kShooterArmKd);

    // private final PIDController m_shooterPID = new PIDController(Configuration.kShooterArmKp,
    //     Configuration.kShooterArmKi, Configuration.kShooterArmKd);

    // private final IntakeSubsystem m_intake;
    // private final DigitalInput m_noteSensor;
    // private final LimelightSubsystem m_limelight;

    private int anglePosition = 0;
    private final double[] anglePositions = Configuration.kShooterArmPositions;

    private final int[] shooterMotorChannels = Configuration.kShooterMotorChannels;
    private final int shooterArmMotorChannel = Configuration.kShooterArmMotorChannel;

    double m_desiredAngle = 0;

    /** Creates a new ShooterSubsystem2. */
    public ShooterSubsystem2() {
        // Make a CANSparkMax that controls both of the shooter motors
        m_shooter = new CANSparkMax(shooterMotorChannels[0], MotorType.kBrushless);
        m_shooter.setSmartCurrentLimit(Configuration.NeoLimit);
        m_shooter2 = new CANSparkMax(shooterMotorChannels[1], MotorType.kBrushless);
        m_shooter2.setSmartCurrentLimit(Configuration.NeoLimit);

        m_shooter2.follow(m_shooter);

        m_armMotor = new CANSparkMax(shooterArmMotorChannel, MotorType.kBrushless);
        m_armMotor.setSmartCurrentLimit(Configuration.NeoLimit);
        
        m_armEnc = m_armMotor.getEncoder();
        m_armEnc.setPositionConversionFactor(360/230 * 1.5);
        m_armEnc.setPosition(0);

        m_shooterEnc = m_shooter.getEncoder();
    }

    public void periodic() {
        processControl();
    }

    private void processControl() {
        // Drive the motors to the shooters angle to whatever the target angle is
        double currentAngle = m_armEnc.getPosition();
        double output = m_shooterPID.calculate(currentAngle, m_desiredAngle);

        // Limit the PID output
        if (output > Configuration.kArmMotorLimit)
        {
            output = Configuration.kArmMotorLimit;
        }
        else if (output < -Configuration.kArmMotorLimit)
        {
            output = -Configuration.kArmMotorLimit;
        }

        SmartDashboard.putNumber("m_desiredAngle", m_desiredAngle);
        SmartDashboard.putNumber("currentAngle", currentAngle);
        SmartDashboard.putNumber("armPID", output);
        SmartDashboard.putBoolean("armLowerLimit", m_lowLimitSwitch.get());
        SmartDashboard.putBoolean("armUpperLimit", m_upperLimitSwitch.get());
        m_armMotor.set(output);
    }

    // Spin the shooter motor and return true when the velocity is above the minimum
    public Command spinShooter(boolean force) {
        return new FunctionalCommand(
            () -> m_shooter.set(-1),
            () -> {},
            interrupted -> m_shooter.set(0),
            () -> !force && Math.abs(m_shooterEnc.getVelocity()) >= Configuration.kMinFlywheelSpeed,
            this
        );
    }

    public Command unhookShooter() {
        return new FunctionalCommand(
            () -> {m_desiredAngle = Configuration.kUnhookPosition;},
            () -> {},
            interrupted -> {},
            () -> m_armEnc.getPosition() >=
                Configuration.kUnhookPosition - Configuration.kTargetError,

            this
        );
    }

    public Command lowerShooter() {
        return new FunctionalCommand(
            () -> {m_desiredAngle = Configuration.kLoweredPosition;},
            () -> {},
            interrupted -> {},
            () -> m_armEnc.getPosition()
                <= Configuration.kLoweredPosition + Configuration.kTargetError,
                
            this
        );
    }

    public void setPosition(double speed) {
        if (speed > 0)
        {
            if (m_lowLimitSwitch.get()) {
                m_desiredAngle -= speed / Configuration.kArmAngleFactor;
            } 
        }
        else {
            // If the arm is past the backwards software limit, set it to the software limit
            if (m_upperLimitSwitch.get()) {
                m_desiredAngle -= speed / Configuration.kArmAngleFactor;
            }
        }
    }

    // public Command raiseShooter(double rate) {
    //     return new InstantCommand(() -> m_desiredAngle += rate);
    // }

    // public Command lowerShooter(double rate) {
    //     return new InstantCommand(() -> m_desiredAngle += rate);
    // }

    // public Command movePositionUp() {
    //     anglePosition++;
    //     movePosition();

    //     return new InstantCommand();
    // }

    // public Command movePositionDown() {
    //     anglePosition--;
    //     movePosition();

    //     return new InstantCommand();
    // }
}
