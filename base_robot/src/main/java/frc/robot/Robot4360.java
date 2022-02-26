// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.lang.Math;
import java.util.Map;

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot4360 extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private XboxController m_xbox;
  private XboxController m_xbox1;

  private final CANSparkMax m_leftMotor1 = new CANSparkMax(1, MotorType.kBrushless);
  private final CANSparkMax m_leftMotor2 = new CANSparkMax(2, MotorType.kBrushless);
  private final MotorControllerGroup m_left = new MotorControllerGroup(m_leftMotor1, m_leftMotor2);

  private final CANSparkMax m_rightMotor1 = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax m_rightMotor2 = new CANSparkMax(4, MotorType.kBrushless);
  private final MotorControllerGroup m_right = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);

  private final TalonSRX m_harvester = new TalonSRX(10);
  private final TalonSRX m_mainClimber1 = new TalonSRX(11);
  private final TalonSRX m_mainClimber2 = new TalonSRX(12);
  private final TalonSRX m_auxClimber = new TalonSRX(13);

  private final TalonSRX m_m1 = new TalonSRX(14);
  private final TalonSRX m_m2 = new TalonSRX(15);
  private final TalonSRX m_m3 = new TalonSRX(16);

  RelativeEncoder m_lenc = m_leftMotor1.getEncoder();
  RelativeEncoder m_renc = m_rightMotor1.getEncoder();
  
  NetworkTableEntry m_autoTime;
  NetworkTableEntry m_lencTable;
  NetworkTableEntry m_rencTable;
  NetworkTableEntry m_position;

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_right.setInverted(true);

    m_myRobot = new DifferentialDrive(m_left, m_right);
    m_myRobot.setDeadband(0.1);
    m_xbox = new XboxController(0);
    m_xbox1 = new XboxController(1);

    m_lenc.setPositionConversionFactor(1/2.1);
    m_renc.setPositionConversionFactor(1/2.1);
    m_lenc.setPosition(0);
    m_renc.setPosition(0);

    TalonSRXConfiguration config = new TalonSRXConfiguration();
    config.peakCurrentLimit = 40; // the peak current, in amps
    config.peakCurrentDuration = 1500; // the time at the peak current before the limit triggers, in ms
    config.continuousCurrentLimit = 30; // the current to maintain if the peak limit is triggered
    m_mainClimber1.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    m_mainClimber2.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    m_auxClimber.configAllSettings(config);

    //Get the default instance of NetworkTables that was created automatically
    //when your program starts
    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    // Attempting to get the driver station position
    NetworkTable fmsInfo = inst.getTable("FMSInfo");
    NetworkTableEntry m_driverStationPos = fmsInfo.getEntry("StationNumber");
    Number pos = m_driverStationPos.getNumber(0);

    System.out.println("Driver Station number: " + pos.toString());
    System.out.println("Robot starting");

    m_position = Shuffleboard.getTab("RoboInfo")
    .add("RobotPosition", 3)
    .withWidget(BuiltInWidgets.kNumberSlider)
    .withProperties(Map.of("min", 0, "max", 3)) // specify widget properties here
    .getEntry();

    System.out.println("Robot position: " + m_position.getNumber(-1).toString());

    m_rencTable = Shuffleboard.getTab("RoboInfo").add("Right Encoder", 0).getEntry();
    m_lencTable = Shuffleboard.getTab("RoboInfo").add("Left Encoder", 0).getEntry();
    m_autoTime = Shuffleboard.getTab("RoboInfo").add("Time", 0).getEntry();
  }

  @Override
  public void teleopPeriodic() {
    m_myRobot.arcadeDrive(m_xbox.getLeftY(), m_xbox.getLeftX());
    m_lencTable.setNumber(m_lenc.getPosition());
    m_rencTable.setNumber(m_lenc.getPosition());
 
    double harvester_speed = m_xbox.getRightX();
    if (Math.abs(harvester_speed) > 0.2)
    {
      m_harvester.set(TalonSRXControlMode.PercentOutput, harvester_speed);
    }
    else
    {
      m_harvester.set(TalonSRXControlMode.PercentOutput, 0);
    }
    double rightTrigger = m_xbox.getRightTriggerAxis();
    double leftTrigger = m_xbox.getLeftTriggerAxis();

    if (rightTrigger > 0.2)
    {
      m_mainClimber1.set(TalonSRXControlMode.PercentOutput, rightTrigger);
      m_mainClimber2.set(TalonSRXControlMode.PercentOutput, rightTrigger);
    }
    else if (leftTrigger > 0.2)
    {
      m_mainClimber1.set(TalonSRXControlMode.PercentOutput, -leftTrigger);
      m_mainClimber2.set(TalonSRXControlMode.PercentOutput, -leftTrigger);
    }
    else
    {
      m_mainClimber1.set(TalonSRXControlMode.PercentOutput, 0);
      m_mainClimber2.set(TalonSRXControlMode.PercentOutput, 0);
    }

    if (m_xbox.getPOV() == 90)
    {
      m_m1.set(TalonSRXControlMode.PercentOutput, 1.0);
    }
    else
    {
      m_m1.set(TalonSRXControlMode.PercentOutput, 0);
    }
    if (m_xbox.getPOV() == 180)
    {
      m_m2.set(TalonSRXControlMode.PercentOutput, 1.0);
    }
    else
    {
      m_m2.set(TalonSRXControlMode.PercentOutput, 0);
    }
    if (m_xbox.getPOV() == 270)
    {
      m_m3.set(TalonSRXControlMode.PercentOutput, 1.0);
    }
    else
    {
      m_m3.set(TalonSRXControlMode.PercentOutput, 0);
    }
  
    SmartDashboard.putNumber("Climber Current", m_mainClimber1.getStatorCurrent());
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

    m_mainClimber2.set(TalonSRXControlMode.PercentOutput, 0.5);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    double stop = System.nanoTime() / 1E9;

    if ((stop - m_start) > 5)
    {
      m_mainClimber2.set(TalonSRXControlMode.PercentOutput, 0);
    }

    //Shuffleboard.getTab("RoboInfo").add("Left Encoder", m_lenc.getPosition());
    //Shuffleboard.getTab("RoboInfo").add("Right Encoder", m_renc.getPosition());
    m_autoTime.setNumber(stop);
  }

}
