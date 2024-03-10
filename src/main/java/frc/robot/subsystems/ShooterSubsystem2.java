package frc.robot.subsystems;

import frc.robot.Configuration;
import frc.robot.LoggedNumber;
import frc.robot.Robot;
import edu.wpi.first.math.MathUtil;
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

    private final int[] shooterMotorChannels = Configuration.kShooterMotorCanID;
    private final int shooterArmMotorChannel = Configuration.kShooterArmMotorCanID;

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
        MathUtil.clamp(m_desiredAngle, Configuration.kLoweredLimit,
            Configuration.kUpperLimit);

        double currentAngle = m_armEnc.getPosition();
        double output = m_shooterPID.calculate(currentAngle, m_desiredAngle);

        // Limit the PID output
        MathUtil.clamp(output, -Configuration.kArmMotorLimit, Configuration.kArmMotorLimit);

        LoggedNumber.getInstance().logNumber("DesiredAngle", m_desiredAngle);
        LoggedNumber.getInstance().logNumber("CurrentAngle", currentAngle);
        LoggedNumber.getInstance().logNumber("ArmPIDOut", output);
        LoggedNumber.getInstance().logNumber("ArmHighLimit", m_upperLimitSwitch.get() ? 1.0 : 0.0);
        LoggedNumber.getInstance().logNumber("ArmLowLimit", m_lowLimitSwitch.get() ? 1.0 : 0);
        LoggedNumber.getInstance().logNumber("ArmCurrent", m_armMotor.getOutputCurrent());
        SmartDashboard.putNumber("m_desiredAngle", m_desiredAngle);
        SmartDashboard.putNumber("currentAngle", currentAngle);
        m_armMotor.set(output);
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

    public Command spinShooterToVelocity(double velocity) {
        return new FunctionalCommand(
            () -> m_shooter.set(velocity),
            () -> {},
            interrupted -> {},
            () -> Math.abs(m_shooterEnc.getVelocity()) >= 15,
            this
        );
    }

    public Command stopShooter() {
        return new InstantCommand(() -> m_shooter.set(0));
    }

    public Command unhookShooter() {
        return new FunctionalCommand(
            () -> {},
            () -> {m_desiredAngle += 0.1;},
            interrupted -> {},
            () -> m_armEnc.getPosition() >=
                Configuration.kUnhookPosition - Configuration.kTargetError,

            this
        );
    }

    public Command lowerShooter() {
        return new FunctionalCommand(
            () -> {},
            () -> {m_desiredAngle -= 0.5;},
            interrupted -> {},
            () -> m_armEnc.getPosition()
                <= Configuration.kLoweredPosition + Configuration.kTargetError,
                
            this
        );
    }

    public Command ampPosition() {
        return new FunctionalCommand(
            () -> {},
            () -> {m_desiredAngle += 0.8;},
            interrupted -> {},
            () -> m_armEnc.getPosition()
                >= Configuration.kAmpPosition + Configuration.kTargetError,
                
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
            if (m_upperLimitSwitch.get()) {
                m_desiredAngle -= speed / Configuration.kArmAngleFactor;
            }
        }
    }

    public Command setShootPosition() {
        return new InstantCommand(() -> m_desiredAngle = Configuration.kShooterArmPositions[0]);
    }

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
