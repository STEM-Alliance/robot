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

public class ShooterSubsystem extends SubsystemBase {
    private final CANSparkMax m_shooter;
    private final CANSparkMax m_shooter2;
    private final CANSparkMax m_armMotor;
    private final RelativeEncoder m_armEnc;
    private final RelativeEncoder m_shooterEnc;

    private final DigitalInput m_limitSwitch = new DigitalInput(Configuration.kArmLimitswitchChannel);

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

    public double setTargetAngle;

    /** Creates a new ShooterSubsystem. */
    public ShooterSubsystem() {
        
        // m_intake = null;
        // m_limelight = null;
        // m_noteSensor = null;
        
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
        SmartDashboard.putNumber("target angle", setTargetAngle);
        SmartDashboard.putNumber("armangle", m_armEnc.getPosition());
        SmartDashboard.putNumber("shooter_rps", m_shooterEnc.getVelocity()/60);
        angleShooter(setTargetAngle);
    }

    /* Only if note is in intake, spin up motors, align with limelight and calculate shooting angle
    When motors up to speed and at target angle, shoot note and move to down position */
    public void shootNoteSpeaker() {
        // Make sure that there is a note in the intake
        // if (!m_noteSensor.get()) {
        //     return;
        // }

        // spin shooter function
        // is at target angle function
        // command for limelight to align to speaker
        // shoot note at speaker command, waits until all true and then
        // runs the feed note into shooter command

        /* Spin up the shooter motors and angle the shooter while
        waiting for the robot to align to the speaker */
        // boolean isShooterReady = spinShooter();
        // boolean isRobotAligned = m_limelight.alignToSpeaker();
        // boolean isShooterAngled = angleShooter(false);

        // // If all the requirements are not met, wait for the next Periodic() loop
        // while (!isShooterReady && isShooterAngled && isRobotAligned) {
        //     // Wait for the next Periodic() loop
        //     return;
        // }

        angleShooter(false);
    }

    public void shootNoteAmp() {
        // if (!m_noteSensor.get()) {
        //     return;
        // }

        // spinShooter()
        // .alongWith(AlignToAmp);
        //.alongWith(angleShooter(false));
        // boolean isRobotAligned = m_limelight.alignToAmp();
        // boolean isShooterAngled = angleShooter(true);
    }

    public void shootNote() {
        return;
    }

    public boolean moveToIntakePos() {
        return angleShooter(anglePositions[0]);
    }

    private double calculateShooterAngle(double Distance) {
        /* Example logarithmic curve with values
        {{0.25, 85}, {0.5, 60}, {1, 55}, {2, 35}} (meters, degrees) */
        return 52.5 - 22.3618 * Math.log(Distance);
    }

    private void angleShooter(boolean isTargetAmp) {
        if (isTargetAmp) {
            angleShooter(anglePositions[2]);
        }

        // double distanceToSpeaker = m_limelight.getDistanceToSpeaker();
        // double targetShooterAngle = calculateShooterAngle(distanceToSpeaker);

        // targetAngle = targetShooterAngle;
    }

    private boolean angleShooter(double targetAngle) {
        // Drive the motors to the shooters angle to whatever the target angle is
        double shooterAngle = m_armEnc.getPosition();

        // If targetAngle within .02 rotations of the shooterAngle
        // if (targetAngle > shooterAngle - 0.01 && targetAngle < shooterAngle + 0.01) {
        double output = m_shooterPID.calculate(shooterAngle, targetAngle);
        SmartDashboard.putNumber("pid out", output);

        m_armMotor.set(output);
            //return false;
        //}

        // Make seperate is at angle function
        return true;
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
            () -> {},
            () -> setPosition(-1),
            interrupted -> {},
            () -> m_armEnc.getPosition() > 5,
            this
        );
    }

    public Command lowerShooter() {
        return new FunctionalCommand(
        () -> setPosition(-1),
        () -> {},
        interrupted -> {},
        () -> setPosition(-1)
        );
    }

    
    public Command raiseShooter(double rate) {
        return new InstantCommand();
    }

    public Command lowerShooter(double rate) {
        return new InstantCommand();
    }

    private void movePosition() {
        // Clamp anglePosition to 0-2

        return;


        // int preClamp = anglePosition;
        // anglePosition = Math.max(Math.min(anglePosition, 0), 2);

        // /* If the value before the clamp is different than the current value, it is out of
        // range and hasnt changed, so it does not need to re-angle the shooter. */
        // if (anglePosition == preClamp) {
        //     angleShooter(anglePositions[anglePosition]);
        // }
    }

    public boolean setPosition(double upDownChange) {
        double shooterAngle = m_armEnc.getPosition();

        upDownChange = -upDownChange;

        if ((m_limitSwitch.get() && upDownChange < 0) || (shooterAngle >= Configuration.kArmBackwardsLimit && upDownChange > 0)){
            return true;
        } else {
            setTargetAngle += upDownChange / 2;
            return false;
        }
    }

    public Command movePositionUp() {
        anglePosition++;
        movePosition();

        return new InstantCommand();
    }

    public Command movePositionDown() {
        anglePosition--;
        movePosition();

        return new InstantCommand();
    }
}
