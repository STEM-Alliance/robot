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
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.revrobotics.*;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.math.filter.SlewRateLimiter;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.lang.Math;

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

  private final CANSparkMax m_mainClimber1 = new CANSparkMax(6, MotorType.kBrushless);
  private final CANSparkMax m_mainClimber2 = new CANSparkMax(7, MotorType.kBrushless);
  private final CANSparkMax m_auxClimber = new CANSparkMax(8, MotorType.kBrushless);

  RelativeEncoder m_lenc = m_leftMotor1.getEncoder();
  RelativeEncoder m_renc = m_rightMotor1.getEncoder();

  SlewRateLimiter m_forwardFilter = new SlewRateLimiter(Configuration.forward_back_slew_rate);
  SlewRateLimiter m_rotationFilter = new SlewRateLimiter(Configuration.right_left_slew_rate);

  NetworkTableEntry m_autoTime;
  NetworkTableEntry m_lencTable;
  NetworkTableEntry m_rencTable;
  NetworkTableEntry m_moveDistance;

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

    m_moveDistance = Shuffleboard.getTab("RoboInfo")
    .add("MoveDistance", 20)
    .getEntry();

    m_rencTable = Shuffleboard.getTab("RoboInfo").add("Right Encoder", 0).getEntry();
    m_lencTable = Shuffleboard.getTab("RoboInfo").add("Left Encoder", 0).getEntry();
    m_autoTime = Shuffleboard.getTab("RoboInfo").add("Time", 0).getEntry();

    m_mainClimber1.setIdleMode(IdleMode.kBrake);
    m_mainClimber2.setIdleMode(IdleMode.kBrake);
    m_auxClimber.setIdleMode(IdleMode.kBrake);

    m_myRobot.setDeadband(0.15);
  }

  @Override
  public void teleopPeriodic() {

    // The rotation needs to be inverted. Otherwise the robot will turn in the wrong direction
    double multiFactor = Configuration.fine_controller_derate;
    if (m_controller1.getYButton())
    {
      multiFactor = 1;
    }
    m_myRobot.arcadeDrive(m_forwardFilter.calculate(m_controller1.getLeftY() * multiFactor), 
                          m_rotationFilter.calculate(-m_controller1.getLeftX()) * multiFactor);
    m_lencTable.setNumber(m_lenc.getPosition());
    m_rencTable.setNumber(m_lenc.getPosition());
 
    double rightTrigger = m_controller1.getRightTriggerAxis();
    double leftTrigger = m_controller1.getLeftTriggerAxis();

    if (rightTrigger > Configuration.controller_dead_zone)
    {
      m_mainClimber1.set(rightTrigger);
      m_mainClimber2.set(rightTrigger);
    }
    else if (leftTrigger > Configuration.controller_dead_zone)
    {
      m_mainClimber1.set(-leftTrigger);
      m_mainClimber2.set(-leftTrigger);
    }
    else
    {
      m_mainClimber1.set(0);
      m_mainClimber2.set(0);
    }

    double aux_arm_speed = m_controller1.getRightY();
    if (Math.abs(aux_arm_speed) > Configuration.controller_dead_zone)
    {
      m_auxClimber.set(aux_arm_speed);
    }
    else
    {
      m_auxClimber.set(0);
    }
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

  double m_start = 0;
  @Override
  public void autonomousInit() {
    m_lenc.setPosition(0);
    m_renc.setPosition(0);

    m_left.set(0);
    m_right.set(0);
    m_start = System.nanoTime() / 1E9;
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    double stop = System.nanoTime() / 1E9;

    if (m_lenc.getPosition() > m_moveDistance.getNumber(0).doubleValue())
    {
      m_myRobot.stopMotor();
    }
    else
    {
      m_myRobot.arcadeDrive(0.5, 0);
    }

    m_lencTable.setNumber(m_lenc.getPosition());
    m_rencTable.setNumber(m_lenc.getPosition());
    m_autoTime.setNumber(stop);
  }

}
