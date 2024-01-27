// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

// Logging stuff
import frc.robot.LoggedNumber;

public class SwerveModule {
  private static final double kModuleMaxAngularVelocity = Configuration.kMaxAngularSpeed;
  private static final double kModuleMaxAngularAcceleration =
      2 * Math.PI; // radians per second squared

  private int m_swerveIndex = -1;

  private final CANSparkMax m_driveMotor;
  private final CANSparkMax m_turningMotor;
  private RelativeEncoder m_driveEncoder;
  public RelativeEncoder m_turningEncoder;

  private final AnalogInput m_absolutePos;

  // Gains are for example purposes only - must be determined for your own robot!
  private final PIDController m_drivePIDController = new PIDController(Configuration.kDriveKp, Configuration.kDriveKi, Configuration.kDriveKd);

  // Gains are for example purposes only - must be determined for your own robot!
  private final ProfiledPIDController m_turningPIDController =
      new ProfiledPIDController(
          Configuration.kSwerveKp,
          Configuration.kSwerveKi,
          Configuration.kSwerveKd,
          new TrapezoidProfile.Constraints(
              kModuleMaxAngularVelocity, kModuleMaxAngularAcceleration));

  // Gains are for example purposes only - must be determined for your own robot!
  private final SimpleMotorFeedforward m_driveFeedforward = new SimpleMotorFeedforward(Configuration.kDriveKs, Configuration.kDriveKv);
  private final SimpleMotorFeedforward m_turnFeedforward = new SimpleMotorFeedforward(Configuration.kSwerveKs, Configuration.kSwerveKv);

  private boolean m_homingMotors = false;

  /**
   * Constructs a SwerveModule with a drive motor, turning motor, drive encoder and turning encoder.
   *
   * @param driveMotorChannel PWM output for the drive motor.
   * @param turningMotorChannel PWM output for the turning motor.
   * @param driveEncoderChannelA DIO input for the drive encoder channel A
   * @param driveEncoderChannelB DIO input for the drive encoder channel B
   * @param turningEncoderChannelA DIO input for the turning encoder channel A
   * @param turningEncoderChannelB DIO input for the turning encoder channel B
   */
  public SwerveModule(
      int swerveIndex,
      int driveMotorChannel,
      int turningMotorChannel,
      int analogInputChannel) {
    m_swerveIndex = swerveIndex;
    m_driveMotor = new CANSparkMax(driveMotorChannel, MotorType.kBrushless);
    m_turningMotor = new CANSparkMax(turningMotorChannel, MotorType.kBrushless);

    m_absolutePos = new AnalogInput(analogInputChannel);

    m_driveEncoder = m_driveMotor.getEncoder();
    m_turningEncoder = m_turningMotor.getEncoder();

    // Set the distance per pulse for the drive encoder. We can simply use the
    // distance traveled for one rotation of the wheel divided by the encoder
    // resolution.
    m_driveEncoder.setPositionConversionFactor(2 * Math.PI * Configuration.kWheelRadius / Configuration.kDriveGearReduction);
    m_driveEncoder.setVelocityConversionFactor(2 * Math.PI * Configuration.kWheelRadius / Configuration.kDriveGearReduction / 60);
    m_driveEncoder.setPosition(0);

    // Set the distance (in this case, angle) in radians per pulse for the turning encoder.
    // This is the the angle through an entire rotation (2 * pi) divided by the
    // encoder resolution.
    m_turningEncoder.setPositionConversionFactor(2 * Math.PI / Configuration.kTurningGearReduction);    
    m_turningEncoder.setPosition(0);

    // Limit the PID Controller's input range between -pi and pi and set the input
    // to be continuous.
    m_turningPIDController.enableContinuousInput(-Math.PI, Math.PI);
  }

  /**
   * Returns the current state of the module.
   *
   * @return The current state of the module.
   */
  public SwerveModuleState getState() {
    return new SwerveModuleState(
        m_driveEncoder.getVelocity(), new Rotation2d(m_turningEncoder.getPosition()));
  }

  /**
   * Returns the current position of the module.
   *
   * @return The current position of the module.
   */
  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(
        m_driveEncoder.getVelocity(), new Rotation2d(m_turningEncoder.getPosition()));
  }

  /**
   * Sets the desired state for the module.
   *
   * @param desiredState Desired state with speed and angle.
   */
  public void setDesiredState(SwerveModuleState desiredState) {
    if (!m_homingMotors)
    {
      var encoderRotation = new Rotation2d(m_turningEncoder.getPosition());

      // Optimize the reference state to avoid spinning further than 90 degrees
      SwerveModuleState state = SwerveModuleState.optimize(desiredState, encoderRotation);

      // Scale speed by cosine of angle error. This scales down movement perpendicular to the desired
      // direction of travel that can occur when modules change directions. This results in smoother
      // driving.
      state.speedMetersPerSecond *= state.angle.minus(encoderRotation).getCos();

      // Calculate the drive output from the drive PID controller.
      final double driveOutput =
          m_drivePIDController.calculate(m_driveEncoder.getVelocity(), state.speedMetersPerSecond);

      final double driveFeedforward = m_driveFeedforward.calculate(state.speedMetersPerSecond);

      // Calculate the turning motor output from the turning PID controller.
      final double turnOutput =
          m_turningPIDController.calculate(m_turningEncoder.getPosition(), state.angle.getRadians());

      final double turnFeedforward =
          m_turnFeedforward.calculate(m_turningPIDController.getSetpoint().velocity);

      //m_driveMotor.set(driveOutput + driveFeedforward);
      m_driveMotor.set(driveFeedforward / 8);
      //m_turningMotor.set(turnOutput + turnFeedforward);
      m_turningMotor.set(turnOutput);

      LoggedNumber.getInstance().logNumber(m_swerveIndex + "_ff", driveFeedforward / 4, true);
      LoggedNumber.getInstance().logNumber(m_swerveIndex + "_abspos", m_absolutePos.getValue(), true);
    }
  }

  public void setGains(double kp, double ki, double kd) {
    m_turningPIDController.setP(kp);
    m_turningPIDController.setI(ki);
    m_turningPIDController.setD(kd);
  }

  // public void homeSwerve(double driveTo) {
  //   m_homingMotors = true;
  //   m_turningMotor.set(driveTo);
  // }

  // public void doneHoming() {
  //   m_turningEncoder.setPosition(0);
  //   m_homingMotors = false;
  // }

 
  private double getSwerveEncoderSyncedPos(double zeroedAbsPos) {
    /* Calculate the error and wrap it around with a modulo
    If the current rotation was 50, and the target was 4096, this
    would set the encoders rotation to ~+0.1 rotations instead of ~-12.5 */

    double diffPos = (getAbsPos() - zeroedAbsPos);
    if (diffPos > 2048)
    {
      diffPos -= 4096;
    }
    return (diffPos * 2 * Math.PI) / 4096;
  }

  public void syncSwerveEncoder(double zeroedAbsPos) {
    m_turningEncoder.setPosition(getSwerveEncoderSyncedPos(zeroedAbsPos));

  }

  public double getAbsPos() {
    return m_absolutePos.getValue();
  }

}
