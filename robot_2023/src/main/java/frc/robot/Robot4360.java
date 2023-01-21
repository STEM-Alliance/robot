// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.revrobotics.*;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.lang.Math;

import edu.wpi.first.cameraserver.CameraServer;
import java.nio.file.Path;
import edu.wpi.first.math.trajectory.*;
import java.io.IOException;
import com.kauailabs.navx.frc.*;
import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.math.geometry.*;

/**
 * Uses the CameraServer class to automatically capture video from a USB webcam and send it to the
 * FRC dashboard without doing any vision processing. This is the easiest way to get camera images
 * to the dashboard. Just add this to the robotInit() method in your program.
 */

 

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot4360 extends TimedRobot {
  //private DifferentialDrive m_myRobot;
  private XboxController m_controller1;
  private XboxController m_controller2;

  private final CANSparkMax m_leftMotor1 = new CANSparkMax(1, MotorType.kBrushless);
  private final CANSparkMax m_leftMotor2 = new CANSparkMax(2, MotorType.kBrushless);
  // Comment this line out for and uncomment the following line for simulation
  private final MotorControllerGroup m_left = new MotorControllerGroup(m_leftMotor1, m_leftMotor2);
  //private final PWMSparkMax m_left = new PWMSparkMax(0);

  private final CANSparkMax m_rightMotor1 = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax m_rightMotor2 = new CANSparkMax(4, MotorType.kBrushless);
  private final MotorControllerGroup m_right = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);
  //private final PWMSparkMax m_right = new PWMSparkMax(1);

  private DifferentialDrive m_myRobot = new DifferentialDrive(m_left, m_right);

  RelativeEncoder m_lenc = m_leftMotor1.getEncoder();
  RelativeEncoder m_renc = m_rightMotor1.getEncoder();

  SlewRateLimiter m_forwardFilter = new SlewRateLimiter(Configuration.forward_back_slew_rate);
  SlewRateLimiter m_rotationFilter = new SlewRateLimiter(Configuration.right_left_slew_rate);

  NetworkTableEntry m_autoTime;
  NetworkTableEntry m_lencTable;
  NetworkTableEntry m_rencTable;
  NetworkTableEntry m_moveDistance;

  Trajectory trajectory = new Trajectory();
  AHRS m_ahrs;

  DifferentialDriveOdometry m_odometry;

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_right.setInverted(true);

    m_controller1 = new XboxController(0);
    m_controller2 = new XboxController(1);

    m_lenc.setPositionConversionFactor(Configuration.encoder_counts_to_inches);
    m_renc.setPositionConversionFactor(Configuration.encoder_counts_to_inches);
    m_lenc.setPosition(0);
    m_renc.setPosition(0);

    try {
      Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve("paths/PathWeaver/output/drive_out.wplib.json");
      trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
   } catch (IOException ex) {
      DriverStation.reportError("Unable to open trajectory: paths/PathWeaver/output/drive_out.wplib.json", ex.getStackTrace());
   }
    // Uncomment this if you are using Talon controllers
    // TalonSRXConfiguration config = new TalonSRXConfiguration();
    // config.peakCurrentLimit = 40; // the peak current, in amps
    // config.peakCurrentDuration = 1500; // the time at the peak current before the limit triggers, in ms
    // config.continuousCurrentLimit = 30; // the current to maintain if the peak limit is triggered
    // m_mainClimber1.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    // m_mainClimber2.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    // m_auxClimber.configAllSettings(config);

    //Get the default instance of NetworkTables that was created automatically
    //when your program starts
    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    // Attempting to get the driver station position
    NetworkTable fmsInfo = inst.getTable("FMSInfo");
    NetworkTableEntry m_driverStationPos = fmsInfo.getEntry("StationNumber");
    Number pos = m_driverStationPos.getNumber(0);

    System.out.println("Driver Station number: " + pos.toString());
    System.out.println("Robot starting");

    // m_moveDistance = Shuffleboard.getTab("RoboInfo")
    // .add("MoveDistance", 20)
    // .getEntry();

    // m_rencTable = Shuffleboard.getTab("RoboInfo").add("Right Encoder", 0).getEntry();
    // m_lencTable = Shuffleboard.getTab("RoboInfo").add("Left Encoder", 0).getEntry();
    // m_autoTime = Shuffleboard.getTab("RoboInfo").add("Time", 0).getEntry();

    m_leftMotor1.setIdleMode(IdleMode.kCoast);
    m_leftMotor2.setIdleMode(IdleMode.kCoast);
    m_rightMotor1.setIdleMode(IdleMode.kCoast);
    m_rightMotor2.setIdleMode(IdleMode.kCoast);

    m_myRobot.setDeadband(0.15);

    try {
      /* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
      /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
      /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
      m_ahrs = new AHRS(SPI.Port.kMXP); 
    } catch (RuntimeException ex ) {
        DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
    }
  
  }

  @Override
  public void teleopPeriodic() {

    // The rotation needs to be inverted. Otherwise the robot will turn in the wrong direction
    double multiFactor = Configuration.fine_controller_derate;
    if (m_controller1.getYButton())
    {
      multiFactor = 1;
    }

    double joy1[] = new double[2];
    joy1[0] = m_controller1.getLeftY();
    joy1[1] = m_controller1.getLeftX();

    SmartDashboard.putNumberArray("Joy1", joy1);

    m_myRobot.arcadeDrive(m_forwardFilter.calculate(m_controller1.getLeftY() * multiFactor), 
                          m_rotationFilter.calculate(m_controller1.getLeftX()) * multiFactor);
    SmartDashboard.putNumber("left_enc", m_lenc.getPosition());
    SmartDashboard.putNumber("right_enc", m_renc.getPosition());
    if (m_controller1.getYButton())
    {
      m_ahrs.reset();
    }
    display_ahrs();
  }


  public void display_ahrs() {
     /* Display 6-axis Processed Angle Data                                      */
     SmartDashboard.putBoolean(  "IMU_Connected",        m_ahrs.isConnected());
     SmartDashboard.putBoolean(  "IMU_IsCalibrating",    m_ahrs.isCalibrating());
     SmartDashboard.putNumber(   "IMU_Yaw",              m_ahrs.getYaw());
     SmartDashboard.putNumber(   "IMU_Pitch",            m_ahrs.getPitch());
     SmartDashboard.putNumber(   "IMU_Roll",             m_ahrs.getRoll());
     
     /* Display tilt-corrected, Magnetometer-based heading (requires             */
     /* magnetometer calibration to be useful)                                   */
     
     SmartDashboard.putNumber(   "IMU_CompassHeading",   m_ahrs.getCompassHeading());
     
     /* Display 9-axis Heading (requires magnetometer calibration to be useful)  */
     SmartDashboard.putNumber(   "IMU_FusedHeading",     m_ahrs.getFusedHeading());

    //  /* These functions are compatible w/the WPI Gyro Class, providing a simple  */
    //  /* path for upgrading from the Kit-of-Parts gyro to the navx-MXP            */
     
    //  SmartDashboard.putNumber(   "IMU_TotalYaw",         m_ahrs.getAngle());
    //  SmartDashboard.putNumber(   "IMU_YawRateDPS",       m_ahrs.getRate());

    //  /* Display Processed Acceleration Data (Linear Acceleration, Motion Detect) */
     
    //  SmartDashboard.putNumber(   "IMU_Accel_X",          m_ahrs.getWorldLinearAccelX());
    //  SmartDashboard.putNumber(   "IMU_Accel_Y",          m_ahrs.getWorldLinearAccelY());
    //  SmartDashboard.putBoolean(  "IMU_IsMoving",         m_ahrs.isMoving());
    //  SmartDashboard.putBoolean(  "IMU_IsRotating",       m_ahrs.isRotating());

    //  /* Display estimates of velocity/displacement.  Note that these values are  */
    //  /* not expected to be accurate enough for estimating robot position on a    */
    //  /* FIRST FRC Robotics Field, due to accelerometer noise and the compounding */
    //  /* of these errors due to single (velocity) integration and especially      */
    //  /* double (displacement) integration.                                       */
     
    //  SmartDashboard.putNumber(   "Velocity_X",           m_ahrs.getVelocityX());
    //  SmartDashboard.putNumber(   "Velocity_Y",           m_ahrs.getVelocityY());
    //  SmartDashboard.putNumber(   "Displacement_X",       m_ahrs.getDisplacementX());
    //  SmartDashboard.putNumber(   "Displacement_Y",       m_ahrs.getDisplacementY());
     
    //  /* Display Raw Gyro/Accelerometer/Magnetometer Values                       */
    //  /* NOTE:  These values are not normally necessary, but are made available   */
    //  /* for advanced users.  Before using this data, please consider whether     */
    //  /* the processed data (see above) will suit your needs.                     */
     
    //  SmartDashboard.putNumber(   "RawGyro_X",            m_ahrs.getRawGyroX());
    //  SmartDashboard.putNumber(   "RawGyro_Y",            m_ahrs.getRawGyroY());
    //  SmartDashboard.putNumber(   "RawGyro_Z",            m_ahrs.getRawGyroZ());
    //  SmartDashboard.putNumber(   "RawAccel_X",           m_ahrs.getRawAccelX());
    //  SmartDashboard.putNumber(   "RawAccel_Y",           m_ahrs.getRawAccelY());
    //  SmartDashboard.putNumber(   "RawAccel_Z",           m_ahrs.getRawAccelZ());
    //  SmartDashboard.putNumber(   "RawMag_X",             m_ahrs.getRawMagX());
    //  SmartDashboard.putNumber(   "RawMag_Y",             m_ahrs.getRawMagY());
    //  SmartDashboard.putNumber(   "RawMag_Z",             m_ahrs.getRawMagZ());
     SmartDashboard.putNumber(   "IMU_Temp_C",           m_ahrs.getTempC());
     
    //  /* Omnimount Yaw Axis Information                                           */
    //  /* For more info, see http://navx-mxp.kauailabs.com/installation/omnimount  */
    //  AHRS.BoardYawAxis yaw_axis = m_ahrs.getBoardYawAxis();
    //  SmartDashboard.putString(   "YawAxisDirection",     yaw_axis.up ? "Up" : "Down" );
    //  SmartDashboard.putNumber(   "YawAxis",              yaw_axis.board_axis.getValue() );
     
    //  /* Sensor Board Information                                                 */
    //  SmartDashboard.putString(   "FirmwareVersion",      m_ahrs.getFirmwareVersion());
     
    //  /* Quaternion Data                                                          */
    //  /* Quaternions are fascinating, and are the most compact representation of  */
    //  /* orientation data.  All of the Yaw, Pitch and Roll Values can be derived  */
    //  /* from the Quaternions.  If interested in motion processing, knowledge of  */
    //  /* Quaternions is highly recommended.                                       */
    //  SmartDashboard.putNumber(   "QuaternionW",          m_ahrs.getQuaternionW());
    //  SmartDashboard.putNumber(   "QuaternionX",          m_ahrs.getQuaternionX());
    //  SmartDashboard.putNumber(   "QuaternionY",          m_ahrs.getQuaternionY());
    //  SmartDashboard.putNumber(   "QuaternionZ",          m_ahrs.getQuaternionZ());
     
    //  /* Connectivity Debugging Support                                           */
    //  SmartDashboard.putNumber(   "IMU_Byte_Count",       m_ahrs.getByteCount());
    //  SmartDashboard.putNumber(   "IMU_Update_Count",     m_ahrs.getUpdateCount());    
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */

  /*
  18 pulses per rotation
  each wheel rotation is about 16 inches
  Therefore we have 16/18 = 0.88888 inches per pulse
  2.25 e-2 meters/pulse
   */ 
  double m_start = 0;
  double m_pulses_to_meters = 0.02257777777777777777777777777778;
  @Override
  public void autonomousInit() {
    m_lenc.setPosition(0);
    m_renc.setPosition(0);

    m_left.set(0);
    m_right.set(0);
    m_start = System.nanoTime() / 1E9;

    m_odometry = new DifferentialDriveOdometry(new Rotation2d(m_ahrs.getFusedHeading()),
                                              m_lenc.getPosition() * m_pulses_to_meters, 
                                              m_renc.getPosition() * m_pulses_to_meters,
                                              new Pose2d(5.0, 13.5, new Rotation2d()));
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    m_odometry.update(new Rotation2d(m_ahrs.getFusedHeading()), m_pulses_to_meters, kDefaultPeriod)
  }

}
