// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.*;

import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.lang.Math;

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot7048 extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private XboxController m_controller1;
  private XboxController m_controller2;

  private final CANSparkMax m_leftMotor1 = new CANSparkMax(1, MotorType.kBrushless);
  private final CANSparkMax m_leftMotor2 = new CANSparkMax(2, MotorType.kBrushless);
  private final MotorControllerGroup m_left = new MotorControllerGroup(m_leftMotor1, m_leftMotor2);

  private final CANSparkMax m_rightMotor1 = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax m_rightMotor2 = new CANSparkMax(4, MotorType.kBrushless);
  private final MotorControllerGroup m_right = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);

  private final MotorController m_launcher = new CANSparkMax(5, MotorType.kBrushless);

  private final TalonSRX m_harvester = new TalonSRX(10);
  private final TalonSRX m_climber = new TalonSRX(11);
  private final TalonSRX m_indexer = new TalonSRX(12);
  //private final TalonSRX m_arm = new TalonSRX(13);

  RelativeEncoder m_lenc = m_leftMotor1.getEncoder();
  RelativeEncoder m_renc = m_rightMotor1.getEncoder();

  NetworkTableEntry m_autoTime;
  NetworkTableEntry m_lencTable;
  NetworkTableEntry m_rencTable;
  NetworkTableEntry m_moveDistance;
  NetworkTableEntry m_launcherSpeed;

  DoubleSolenoid m_harvesterArms = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);
  DoubleSolenoid m_climbingArms  = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_right.setInverted(true);
    m_leftMotor1.setIdleMode(CANSparkMax.IdleMode.kCoast);
    m_leftMotor2.setIdleMode(CANSparkMax.IdleMode.kCoast);
    m_rightMotor1.setIdleMode(CANSparkMax.IdleMode.kCoast);
    m_rightMotor2.setIdleMode(CANSparkMax.IdleMode.kCoast);

    m_myRobot = new DifferentialDrive(m_left, m_right);
    m_myRobot.setDeadband(0.15);
    m_controller1 = new XboxController(0);
    m_controller2 = new XboxController(1);

    m_lenc.setPositionConversionFactor(1/2.1);
    m_renc.setPositionConversionFactor(1/2.1);
    m_lenc.setPosition(0);
    m_renc.setPosition(0);

    TalonSRXConfiguration config = new TalonSRXConfiguration();
    config.peakCurrentLimit = 40; // the peak current, in amps
    config.peakCurrentDuration = 1500; // the time at the peak current before the limit triggers, in ms
    config.continuousCurrentLimit = 30; // the current to maintain if the peak limit is triggered
    m_harvester.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    m_indexer.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    m_climber.configAllSettings(config);

    //Get the default instance of NetworkTables that was created automatically
    //when your program starts
    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    //Get the table within that instance that contains the data. There can
    //be as many tables as you like and exist to make it easier to organize
    //your data. In this case, it's a table called datatable.
    NetworkTable table = inst.getTable("RoboInfo");

    System.out.println("Robot starting");

    m_moveDistance = Shuffleboard.getTab("RoboInfo")
    .add("MoveDistance", 20)
    .getEntry();

    m_launcherSpeed = Shuffleboard.getTab("RoboInfo")
    .add("LauncherSpeed", 0.75)
    .getEntry();

    m_rencTable = Shuffleboard.getTab("RoboInfo").add("Right Encoder", 0).getEntry();
    m_lencTable = Shuffleboard.getTab("RoboInfo").add("Left Encoder", 0).getEntry();
    m_autoTime = Shuffleboard.getTab("RoboInfo").add("Time", 0).getEntry();
  }

  @Override
  public void teleopPeriodic() {
    // Controller 1 will control the robot movement, the arms, and the harvester
    m_lencTable.setNumber(m_lenc.getPosition());
    m_rencTable.setNumber(m_renc.getPosition());
    // The rotation needs to be inverted. Otherwise the robot will turn in the wrong direction
    m_myRobot.arcadeDrive(m_controller1.getLeftY(), -m_controller1.getLeftX());
 
    double harvest_value = m_controller1.getRightY();
    if (Math.abs(harvest_value) > 0.2)
    {
      m_harvester.set(TalonSRXControlMode.PercentOutput, harvest_value * 0.7);
    }
    else
    {
      m_harvester.set(TalonSRXControlMode.PercentOutput, 0);
    }

    if (m_controller1.getAButton())
    {
      m_harvesterArms.set(Value.kForward);
    }
    if (m_controller1.getBButton())
    {
      m_harvesterArms.set(Value.kReverse);
    }


    // Controller 2 will control the indexer, launcher, and climbers
    if (m_controller2.getYButton())
    {
      m_launcher.set(0.8);
      System.out.println("Launcher to 80%");
    }
    else if (m_controller2.getBButton())
    {
      m_launcher.set(0.6);
      System.out.println("Launcher to 60%");
    }
    else if (m_controller2.getAButton())
    {
      m_launcher.set(0.4);
      System.out.println("Launcher to 40%");
    }
    else
    {
      m_launcher.set(0);
    }

    double indexer_value = m_controller2.getLeftY();
    if (Math.abs(indexer_value) > 0.25)
    {
      indexer_value *= 0.2;
      m_indexer.set(TalonSRXControlMode.PercentOutput, indexer_value);
    }
    else 
    {
      m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
    }

    double climber_value = m_controller2.getRightY();
    if (Math.abs(climber_value) > 0.25)
    {
      m_climber.set(TalonSRXControlMode.PercentOutput, climber_value);
    }
    else
    {
      m_climber.set(TalonSRXControlMode.PercentOutput, 0);
    }

    if (m_controller2.getRightBumper())
    {
      m_climbingArms.set(Value.kForward);
    }
    if (m_controller2.getLeftBumper())
    {
      m_climbingArms.set(Value.kReverse);
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

    m_right.set(0);
    m_start = System.nanoTime() / 1E9;
    m_harvester.set(TalonSRXControlMode.PercentOutput, 0.5);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    double stop = System.nanoTime() / 1E9;
    
    if (m_lenc.getPosition() > m_moveDistance.getNumber(0).doubleValue())
    {
      m_myRobot.stopMotor();
      m_launcher.set(m_launcherSpeed.getNumber(0.75).doubleValue());
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
