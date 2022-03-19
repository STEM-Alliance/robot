// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.*;

import com.revrobotics.*;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.lang.Math;

import javax.lang.model.util.ElementScanner6;
import javax.swing.RowFilter.ComparisonType;

import edu.wpi.first.wpilibj.Timer;

/**
 * This is a demo program showing the use of the DifferentialDrive class,
 * specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot7048 extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private XboxController m_controller1;
  private XboxController m_controller2;

  private final CANSparkMax m_leftMotor1 = new CANSparkMax(1, MotorType.kBrushless);
  private final CANSparkMax m_leftMotor2 = new CANSparkMax(2, MotorType.kBrushless);
  private final MotorControllerGroup m_left = new MotorControllerGroup(m_leftMotor1, m_leftMotor2);
  // private final PWMSparkMax m_left = new PWMSparkMax(1);

  private final CANSparkMax m_rightMotor1 = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax m_rightMotor2 = new CANSparkMax(4, MotorType.kBrushless);
  private final MotorControllerGroup m_right = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);
  // private final PWMSparkMax m_right = new PWMSparkMax(3);

  private final MotorController m_launcher = new CANSparkMax(5, MotorType.kBrushless);

  private final TalonSRX m_harvester = new TalonSRX(10);
  private final TalonSRX m_climber = new TalonSRX(11);
  private final TalonSRX m_indexer = new TalonSRX(12);
  // private final TalonSRX m_arm = new TalonSRX(13);

  RelativeEncoder m_lenc = m_leftMotor1.getEncoder();
  RelativeEncoder m_renc = m_rightMotor1.getEncoder();

  NetworkTableEntry m_lencTable;
  NetworkTableEntry m_rencTable;
  NetworkTableEntry m_moveDistance;
  NetworkTableEntry m_launcherSpeed;
  NetworkTableEntry m_slewRateLimit;
  NetworkTableEntry m_distanceToBall;
  NetworkTableEntry m_distanceToBasket;
  NetworkTableEntry m_autoLaunchDelay;

  DoubleSolenoid m_harvesterArms = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);
  DoubleSolenoid m_climbingArms = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  SlewRateLimiter m_forwardFilter = new SlewRateLimiter(1.5);
  SlewRateLimiter m_rotationFilter = new SlewRateLimiter(1.5);
  Timer timer = new Timer();

  private AutoStates m_autoStates = AutoStates.IDLE;
  private double m_currentEncPos;

  private double m_initialTime;

  public void setMotorsToBrake(IdleMode mode)
  {
    m_leftMotor1.setIdleMode(mode);
    m_leftMotor2.setIdleMode(mode);
    m_rightMotor1.setIdleMode(mode);
    m_rightMotor2.setIdleMode(mode);
  }

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    CameraServer.startAutomaticCapture();
    m_right.setInverted(true);
    setMotorsToBrake(CANSparkMax.IdleMode.kCoast);
    // m_leftMotor1.setIdleMode(CANSparkMax.IdleMode.kCoast);
    // m_leftMotor2.setIdleMode(CANSparkMax.IdleMode.kCoast);
    // m_rightMotor1.setIdleMode(CANSparkMax.IdleMode.kCoast);
    // m_rightMotor2.setIdleMode(CANSparkMax.IdleMode.kCoast);
    //m_climber.setNeutralMode(NeutralMode.Brake);

    m_myRobot = new DifferentialDrive(m_left, m_right);
    m_myRobot.setDeadband(0.15);
    m_controller1 = new XboxController(0);
    m_controller2 = new XboxController(1);

    m_lenc.setPositionConversionFactor(2);
    m_renc.setPositionConversionFactor(2);
    m_lenc.setPosition(0);
    m_renc.setPosition(0);

    TalonSRXConfiguration config = new TalonSRXConfiguration();
    config.peakCurrentLimit = 40; // the peak current, in amps
    config.peakCurrentDuration = 1500; // the time at the peak current before the limit triggers, in ms
    config.continuousCurrentLimit = 30; // the current to maintain if the peak limit is triggered
    m_harvester.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    m_indexer.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    m_climber.configAllSettings(config);

    System.out.println("Robot starting");

    m_moveDistance = Shuffleboard.getTab("RoboCfg")
        .addPersistent("MoveDistance", 20)
        .getEntry();

    m_launcherSpeed = Shuffleboard.getTab("RoboCfg")
        .addPersistent("LauncherSpeed", 0.75)
        .getEntry();

    m_slewRateLimit = Shuffleboard.getTab("RoboCfg")
        .addPersistent("SlewRateLimit", 1.5)
        .getEntry();

    m_distanceToBall = Shuffleboard.getTab("RoboCfg")
        .addPersistent("DistanceToBall", 42.7)
        .getEntry();

    m_distanceToBasket = Shuffleboard.getTab("RoboCfg")
        .addPersistent("DistanceToBasket", 118.1)
        .getEntry();

    m_autoLaunchDelay = Shuffleboard.getTab("RoboCfg")
        .addPersistent("AutoLaunchDelay", 7)
        .getEntry();

    m_rencTable = Shuffleboard.getTab("RoboInfo").add("Right Encoder", 0).getEntry();
    m_lencTable = Shuffleboard.getTab("RoboInfo").add("Left Encoder", 0).getEntry();

    // m_forwardFilter = new
    // SlewRateLimiter(m_slewRateLimit.getNumber(2).doubleValue());
    // m_rotationFilter = new
    // SlewRateLimiter(m_slewRateLimit.getNumber(2).doubleValue());
  }

  @Override
  public void teleopPeriodic() {
    // Controller 1 will control the robot movement, the arms, and the harvester
    m_lencTable.setNumber(m_lenc.getPosition());
    m_rencTable.setNumber(m_renc.getPosition());
    setMotorsToBrake(CANSparkMax.IdleMode.kCoast);
    // The rotation needs to be inverted. Otherwise the robot will turn in the wrong
    // direction
    double multiFactor = 0.6;
    if (m_controller1.getYButton()) {
      multiFactor = 1;
    }
    // m_myRobot.arcadeDrive(m_forwardFilter.calculate(m_controller1.getLeftY() *
    // multiFactor),
    // m_rotationFilter.calculate(-m_controller1.getLeftX()) * multiFactor);

    m_myRobot.arcadeDrive(m_controller1.getLeftY(), (-m_controller1.getLeftX() * .75));

    double harvest_value = m_controller1.getRightY();
    if (Math.abs(harvest_value) > 0.2) {
      m_harvester.set(TalonSRXControlMode.PercentOutput, harvest_value * 0.45);
      m_harvesterArms.set(Value.kReverse);
    } else {
      m_harvester.set(TalonSRXControlMode.PercentOutput, 0);
      m_harvesterArms.set(Value.kForward);
    }

    if (m_controller1.getAButton()) {
      // up
      m_harvesterArms.set(Value.kForward);
    }
    if (m_controller1.getBButton()) {
      // down
      m_harvesterArms.set(Value.kReverse);
    }

    // Controller 2 will control the indexer, launcher, and climbers
    if (m_controller2.getYButton()) {
      m_launcher.set(0.8);
      System.out.println("Launcher to 80%");
    } else if (m_controller2.getBButton()) {
      m_launcher.set(0.6);
      System.out.println("Launcher to 60%");
    } else if (m_controller2.getAButton()) {
      m_launcher.set(0.4);
      System.out.println("Launcher to 40%");
    } else {
      m_launcher.set(0);
    }

    double indexer_value = m_controller2.getLeftY();
    if (Math.abs(indexer_value) > 0.25) {
      indexer_value *= 0.50;
      m_indexer.set(TalonSRXControlMode.PercentOutput, indexer_value);
    } else {
      m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
    }

    double climber_value = m_controller2.getRightY();
    if (Math.abs(climber_value) > 0.25) {
      m_climber.set(TalonSRXControlMode.PercentOutput, climber_value * 1);
    } else {
      m_climber.set(TalonSRXControlMode.PercentOutput, 0);
    }

    if (m_controller2.getRightBumper()) {
      m_climbingArms.set(Value.kForward);
    }
    if (m_controller2.getLeftBumper()) {
      m_climbingArms.set(Value.kReverse);
    }
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */

  double m_start = 0;

  enum AutoStates {
    IDLE,
    SPIN_UP_DELAY,
    PICKUP_BALL,
    DRIVE_TO_BASKET,
    SHOOT_BALLS,
    DRIVE_OUT_AREA,
    STOP
  }

  @Override
  public void autonomousInit() {
    m_lenc.setPosition(0);
    m_renc.setPosition(0);
    timer.reset();
    timer.start();
    m_right.set(0);
    m_start = System.nanoTime() / 1E9;
    setMotorsToBrake(CANSparkMax.IdleMode.kBrake);
  }

  /** This function is called periodically during autonomous. */
  @Override

  public void autonomousPeriodic() {
    System.out.println(m_lenc.getPosition());
    m_harvesterArms.set(Value.kReverse);
    switch (m_autoStates) {
      case IDLE:
        // This is the start of auto mode, shoot ball
        m_launcher.set(m_launcherSpeed.getNumber(0.75).doubleValue());
        if (timer.get() > 1) {
          m_indexer.set(TalonSRXControlMode.PercentOutput, -0.75);
        }
        m_lenc.setPosition(0);
        m_renc.setPosition(0);
        if (timer.get() > 3) {
          m_autoStates = AutoStates.PICKUP_BALL;
          m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
        }
        break;
      case PICKUP_BALL:
        // Drive forward 42.7 inches, zero encoders before next state
        m_harvester.set(TalonSRXControlMode.PercentOutput, -0.60);
        m_harvesterArms.set(Value.kReverse);
        m_myRobot.arcadeDrive(-0.4, 0);
        if (Math.abs(m_lenc.getPosition()) >= 48) {
          m_myRobot.arcadeDrive(0, 0);
          // m_currentEncPos = m_lenc.getPosition();
          m_harvester.set(TalonSRXControlMode.PercentOutput, 0);
          m_harvesterArms.set(Value.kForward);
          m_autoStates = AutoStates.DRIVE_TO_BASKET;
          m_lenc.setPosition(0);
          m_renc.setPosition(0);
        }
        break;
      case DRIVE_TO_BASKET:
        // Drive forward move distance
        m_myRobot.arcadeDrive(0.4, 0);
        if (Math.abs(m_lenc.getPosition()) > 48) {
          m_myRobot.arcadeDrive(0, 0);
          m_currentEncPos = m_lenc.getPosition();
          m_indexer.set(TalonSRXControlMode.PercentOutput, -0.75);
          m_launcher.set(m_launcherSpeed.getNumber(0.75).doubleValue());
          m_autoStates = AutoStates.SHOOT_BALLS;
          m_initialTime = timer.get();
          
          // m_start = System.nanoTime() / 1E9;
        }
        break;
      case SHOOT_BALLS:
        // Start launcher, and start indexer for shoot_basket time variable
        m_launcher.set(m_launcherSpeed.getNumber(0.75).doubleValue());
        if (timer.get() >= (m_initialTime + 3)) {
          m_autoStates = AutoStates.DRIVE_OUT_AREA;
          m_lenc.setPosition(0);
          m_renc.setPosition(0);
        }
        break;
        
      case DRIVE_OUT_AREA:
        // Drive back out move distance + additional distance
        
        m_myRobot.arcadeDrive(-0.75, 0);
        if (Math.abs(m_lenc.getPosition()) >= 55) {
          m_myRobot.arcadeDrive(0, 0);
        } 
        break;
      default:
        // Do nothing, wait until auto period is done
        break;

    }

    m_lencTable.setNumber(m_lenc.getPosition());
    m_rencTable.setNumber(m_lenc.getPosition());

  }

  public void oldAutonmousPeriodic() {
    // double time = System.nanoTime() / 1E9;
    m_harvester.set(TalonSRXControlMode.PercentOutput, -0.3);
    m_harvesterArms.set(Value.kReverse);
    // Drive to ball .45sec at .5 speed = 3.3ft

    if (timer.get() < 0.45) {
      m_myRobot.arcadeDrive(-0.7, 0);
    }
    // Drive to hanger 0.98sec at .5speed = 7.3ft
    else if (0.45 <= timer.get() && timer.get() < 1.43) {
      m_myRobot.arcadeDrive(0.5, 0);
      m_harvester.set(TalonSRXControlMode.PercentOutput, 0);

    } else if (1.43 <= timer.get() && timer.get() < 5.5) {
      m_launcher.set(0.75);
      m_indexer.set(TalonSRXControlMode.PercentOutput, -0.75);
    } else if (5.5 <= timer.get() && timer.get() < 6.48) {
      m_myRobot.arcadeDrive(-.5, 0);
    } else if (6.48 <= timer.get()) {
      m_myRobot.arcadeDrive(0, 0);
      m_launcher.set(0);
      m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
    }

    /* --- JEREMY START --- */
    // Fire
    if (timer.get() < 2.0) {
      m_launcher.set(0.75);

    }

    else if (timer.get() < 4.0) {

      m_indexer.set(TalonSRXControlMode.PercentOutput, -0.75);
    } else if (timer.get() < 4.75) {
      // Drive backwards, turn off shooter
      m_myRobot.arcadeDrive(-1, 0);
      m_launcher.set(0);
      m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
      m_harvester.set(TalonSRXControlMode.PercentOutput, -0.3);
    } else if (timer.get() < 6.25 /* /)/* && m_lenc.xxx < xxx */) {
      // Chill
      m_myRobot.arcadeDrive(0, 0);
    } else if (timer.get() < 7.65) {
      // Go formard
      m_myRobot.arcadeDrive(.5, 0);

    } else if (timer.get() < 7.85 /* && m_lenc.xxx > xxx */) {
      // Stop and shoot
      m_myRobot.arcadeDrive(0, 0);
      m_launcher.set(0.75);
    } else if (timer.get() < 10.75) {
      m_indexer.set(TalonSRXControlMode.PercentOutput, -0.75);
      m_harvester.set(TalonSRXControlMode.PercentOutput, 0);

    } else if (timer.get() < 12.75) {
      // Turn off the shooter
      m_launcher.set(0);
      m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
    }

    /* --- JEREMY ABOVE THIS --- */

  }

}
