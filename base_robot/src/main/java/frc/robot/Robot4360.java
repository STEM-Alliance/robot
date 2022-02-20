// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.PWMTalonSRX;

import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot4360 extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private XboxController m_xbox;

  private final CANSparkMax m_leftMotor1 = new CANSparkMax(1, MotorType.kBrushless);
  private final CANSparkMax m_leftMotor2 = new CANSparkMax(2, MotorType.kBrushless);
  private final MotorControllerGroup m_left = new MotorControllerGroup(m_leftMotor1, m_leftMotor2);

  private final CANSparkMax m_rightMotor1 = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax m_rightMotor2 = new CANSparkMax(4, MotorType.kBrushless);
  private final MotorControllerGroup m_right = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);

  private final TalonSRX m_harvester = new TalonSRX(10);
  private final TalonSRX m_climber = new TalonSRX(11);
  private final TalonSRX m_indexer = new TalonSRX(12);
  private final TalonSRX m_arm = new TalonSRX(13);

  private final TalonSRX m_m1 = new TalonSRX(14);
  private final TalonSRX m_m2 = new TalonSRX(15);
  private final TalonSRX m_m3 = new TalonSRX(16);

  NetworkTableEntry m_leftEncoder;
  NetworkTableEntry m_rightEncoder;
  NetworkTableEntry m_timeInfo;

  RelativeEncoder m_lenc = m_leftMotor1.getEncoder();
  RelativeEncoder m_renc = m_rightMotor1.getEncoder();


  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_right.setInverted(true);

    m_myRobot = new DifferentialDrive(m_left, m_right);
    m_myRobot.setDeadband(0.1);
    m_xbox = new XboxController(0);

    m_lenc.setPositionConversionFactor(1/2.1);
    m_renc.setPositionConversionFactor(1/2.1);
    m_lenc.setPosition(0);
    m_renc.setPosition(0);

    //Get the default instance of NetworkTables that was created automatically
    //when your program starts
    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    //Get the table within that instance that contains the data. There can
    //be as many tables as you like and exist to make it easier to organize
    //your data. In this case, it's a table called datatable.
    NetworkTable table = inst.getTable("RoboInfo");

    //Get the entries within that table that correspond to the X and Y values
    //for some operation in your program.
    m_leftEncoder = table.getEntry("left");
    m_rightEncoder = table.getEntry("right");

    m_timeInfo = table.getEntry("timeInfo");

    System.out.println("Robot starting");
  }

  @Override
  public void teleopPeriodic() {
    m_myRobot.arcadeDrive(m_xbox.getLeftY(), m_xbox.getLeftX());
    m_leftEncoder.setNumber(m_lenc.getPosition());
    m_rightEncoder.setNumber(m_renc.getPosition());
 
    if (m_xbox.getRightY() > 0.2)
    {
      m_harvester.set(TalonSRXControlMode.PercentOutput, m_xbox.getRightY() * 0.7);
    }
    else
    {
      m_harvester.set(TalonSRXControlMode.PercentOutput, 0);
    }
    if (m_xbox.getRightBumper())
    {
      m_climber.set(TalonSRXControlMode.PercentOutput, 1.0);
    }
    else
    {
      m_climber.set(TalonSRXControlMode.PercentOutput, 0);
    }

    if (m_xbox.getRightBumper())
    {
      m_indexer.set(TalonSRXControlMode.PercentOutput, -0.2);
    }
    else 
    {
      m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
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
    // m_autoSelected = m_chooser.getSelected();
    // // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    // System.out.println("Auto selected: " + m_autoSelected);
    m_lenc.setPosition(0);
    m_renc.setPosition(0);

    m_right.set(0);
    m_start = System.nanoTime() / 1E9;
    m_harvester.set(TalonSRXControlMode.PercentOutput, 0.5);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    double stop = System.nanoTime() / 1E9;
    m_timeInfo.setNumber(stop);

    if ((stop - m_start) > 10)
    {
      m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
    }
    else if ((stop - m_start) > 5)
    {
      m_harvester.set(TalonSRXControlMode.PercentOutput, 0);
      m_indexer.set(TalonSRXControlMode.PercentOutput, -0.2);
    }

    m_leftEncoder.setNumber(m_lenc.getPosition());
    m_rightEncoder.setNumber(m_renc.getPosition());
  }

}
