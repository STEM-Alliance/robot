//package edu.wpi.first.wpilibj.examples.ramsetecommand.subsystems;

package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.kauailabs.navx.frc.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveSubsystem extends SubsystemBase {
    private final CANSparkMax m_leftMotor1 = new CANSparkMax(1, MotorType.kBrushless);
    private final CANSparkMax m_leftMotor2 = new CANSparkMax(2, MotorType.kBrushless);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_leftMotor1, m_leftMotor2);

    private final CANSparkMax m_rightMotor1 = new CANSparkMax(3, MotorType.kBrushless);
    private final CANSparkMax m_rightMotor2 = new CANSparkMax(4, MotorType.kBrushless);
    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);
  
    // The robot's drive
    private final DifferentialDrive m_drive = new DifferentialDrive(m_leftMotors, m_rightMotors);

    // The gyro sensor
    private AHRS m_ahrs;

    RelativeEncoder m_leftEncoder = m_leftMotor1.getEncoder();
    RelativeEncoder m_rightEncoder = m_rightMotor1.getEncoder();
  
    // Odometry class for tracking robot pose
    private final DifferentialDriveOdometry m_odometry;

    /** Creates a new DriveSubsystem. */
    public DriveSubsystem() {
        // We need to invert one side of the drivetrain so that positive voltages
        // result in both sides moving forward. Depending on how your robot's
        // gearbox is constructed, you might have to invert the left side instead.
        m_rightMotors.setInverted(true);

        // Sets the distance per pulse for the encoders
        m_leftEncoder.setPositionConversionFactor(Configuration.encoder_counts_to_inches);
        m_rightEncoder.setPositionConversionFactor(Configuration.encoder_counts_to_inches);

        try {
            /* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
            /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
            /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
            m_ahrs = new AHRS(SPI.Port.kMXP); 
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
        }
        
        m_ahrs.reset();
  

        resetEncoders();
        m_odometry =
            new DifferentialDriveOdometry(
                new Rotation2d(m_ahrs.getFusedHeading()), m_leftEncoder.getPosition(), m_rightEncoder.getPosition());
  }

  @Override
  public void periodic() {
    // Update the odometry in the periodic block
    m_odometry.update(
        new Rotation2d(m_ahrs.getFusedHeading()), m_leftEncoder.getPosition(), m_rightEncoder.getPosition());
  }

  /**
   * Returns the currently-estimated pose of the robot.
   *
   * @return The pose.
   */
  public Pose2d getPose() {
    return m_odometry.getPoseMeters();
  }

  /**
   * Returns the current wheel speeds of the robot.
   *
   * @return The current wheel speeds.
   */
  public DifferentialDriveWheelSpeeds getWheelSpeeds() {
    return new DifferentialDriveWheelSpeeds(m_leftEncoder.getVelocity(), m_rightEncoder.getVelocity());
  }

  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    resetEncoders();
    m_odometry.resetPosition(
        new Rotation2d(m_ahrs.getFusedHeading()), m_leftEncoder.getPosition(), m_rightEncoder.getPosition(), pose);
  }

  /**
   * Drives the robot using arcade controls.
   *
   * @param fwd the commanded forward movement
   * @param rot the commanded rotation
   */
  public void arcadeDrive(double fwd, double rot) {
    m_drive.arcadeDrive(fwd, rot);
  }

  /**
   * Controls the left and right sides of the drive directly with voltages.
   *
   * @param leftVolts the commanded left output
   * @param rightVolts the commanded right output
   */
  // public void tankDriveVolts(double leftVolts, double rightVolts) {
  //   m_leftMotors.setVoltage(leftVolts);
  //   m_rightMotors.setVoltage(rightVolts);
  //   m_drive.feed();
  // }

  /** Resets the drive encoders to currently read a position of 0. */
  public void resetEncoders() {
    m_leftEncoder.setPosition(0);
    m_rightEncoder.setPosition(0);
}

  /**
   * Gets the average distance of the two encoders.
   *
   * @return the average of the two encoder readings
   */
  public double getAverageEncoderDistance() {
    return (m_leftEncoder.getPosition() + m_rightEncoder.getPosition()) / 2.0;
  }

  /**
   * Gets the left drive encoder.
   *
   * @return the left drive encoder
   */
  // public Encoder getLeftEncoder() {
  //   //return m_leftEncoder;
  // }

  /**
   * Gets the right drive encoder.
   *
   * @return the right drive encoder
   */
  // public Encoder getRightEncoder() {
  //   //return m_rightEncoder;
  // }

  /**
   * Sets the max output of the drive. Useful for scaling the drive to drive more slowly.
   *
   * @param maxOutput the maximum output to which the drive will be constrained
   */
  public void setMaxOutput(double maxOutput) {
    m_drive.setMaxOutput(maxOutput);
  }

  /** Zeroes the heading of the robot. */
  public void zeroHeading() {
    m_ahrs.reset();
  }

  /**
   * Returns the heading of the robot.
   *
   * @return the robot's heading in degrees, from -180 to 180
   */
  public double getHeading() {
    return m_ahrs.getFusedHeading();
  }

  /**
   * Returns the turn rate of the robot.
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    return -m_ahrs.getRate();
  }

  public void move(double leftSpeed, double rightSpeed)
  {
    double cmdMotorSpeeds[] = new double[2];
    cmdMotorSpeeds[0] = leftSpeed;
    cmdMotorSpeeds[1] = rightSpeed;
    SmartDashboard.putNumberArray("CmdSpeeds", cmdMotorSpeeds);

    m_leftMotors.set(leftSpeed);
    m_rightMotors.set(rightSpeed);
  }

  public void stop()
  {
    System.out.println("Stopping robot");
  }
}