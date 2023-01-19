// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.networktables.NetworkTableEntry;
import java.lang.Math;
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

  private final CANSparkMax m_rightMotor1 = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax m_rightMotor2 = new CANSparkMax(4, MotorType.kBrushless);
  private final MotorControllerGroup m_right = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);

  private final MotorController m_launcher = new CANSparkMax(5, MotorType.kBrushless);

  private final TalonSRX m_harvester = new TalonSRX(10);
  private final TalonSRX m_climber = new TalonSRX(11);
  private final TalonSRX m_indexer = new TalonSRX(12);

  RelativeEncoder m_lenc = m_leftMotor1.getEncoder();
  RelativeEncoder m_renc = m_rightMotor1.getEncoder();

  DoubleSolenoid m_harvesterArms = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);
  DoubleSolenoid m_climbingArms = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

  SlewRateLimiter m_forwardFilter = new SlewRateLimiter(1.5);
  SlewRateLimiter m_rotationFilter = new SlewRateLimiter(1.5);
  Timer timer = new Timer();

  private AutoStates m_autoStates = AutoStates.IDLE;
  private double m_currentEncPos;
  private double m_initialTime;

  private SparkMaxPIDController m_pidController[];

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
    //m_climber.setNeutralMode(NeutralMode.Brake);

    m_myRobot = new DifferentialDrive(m_left, m_right);
    m_myRobot.setDeadband(0.15);
    m_controller1 = new XboxController(0);
    m_controller2 = new XboxController(1);

    m_lenc.setPositionConversionFactor(Configuration.encoder_counts_to_inches);
    m_renc.setPositionConversionFactor(Configuration.encoder_counts_to_inches);
    m_lenc.setPosition(0);
    m_renc.setPosition(0);

    TalonSRXConfiguration config = new TalonSRXConfiguration();
    config.peakCurrentLimit = 40; // the peak current, in amps
    config.peakCurrentDuration = 1500; // the time at the peak current before the limit triggers, in ms
    config.continuousCurrentLimit = 30; // the current to maintain if the peak limit is triggered
    m_harvester.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    m_indexer.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    m_climber.configAllSettings(config);

    // m_pidController[0] = m_leftMotor1.getPIDController();
    // m_pidController[1] = m_leftMotor2.getPIDController();
    // m_pidController[2] = m_rightMotor1.getPIDController();
    // m_pidController[3] = m_rightMotor2.getPIDController();

    System.out.println("Robot starting");

  }

  @Override
  public void teleopPeriodic() {
    // Controller 1 will control the robot movement, the arms, and the harvester
    setMotorsToBrake(CANSparkMax.IdleMode.kCoast);
    // The rotation needs to be inverted. Otherwise the robot will turn in the wrong
    // direction
    double multiFactor = Configuration.fine_controller_derate;
    if (m_controller1.getYButton()) {
      multiFactor = 1;
    }
    // m_myRobot.arcadeDrive(m_forwardFilter.calculate(m_controller1.getLeftY() *
    // multiFactor),
    // m_rotationFilter.calculate(-m_controller1.getLeftX()) * multiFactor);

    m_myRobot.arcadeDrive(m_controller1.getLeftY(), (-m_controller1.getLeftX() * Configuration.right_left_derate_percentage));

    double harvest_value = m_controller1.getRightY();
    if (Math.abs(harvest_value) > Configuration.controller_dead_zone) {
      m_harvester.set(TalonSRXControlMode.PercentOutput, harvest_value * Configuration.harvester_max_speed);
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
      m_launcher.set(Configuration.launcher_high_speed_percentage);
      System.out.println("Launcher to 80%");
    } else if (m_controller2.getBButton()) {
      m_launcher.set(Configuration.launcher_medium_speed_percentage);
      System.out.println("Launcher to 60%");
    } else if (m_controller2.getAButton()) {
      m_launcher.set(Configuration.launcher_low_speed_percentage);
      System.out.println("Launcher to 40%");
    } else {
      m_launcher.set(0);
    }

    double indexer_value = m_controller2.getLeftY();
    if (Math.abs(indexer_value) > Configuration.controller_dead_zone) {
      indexer_value *= Configuration.indexer_max_speed;
      m_indexer.set(TalonSRXControlMode.PercentOutput, indexer_value);
    } else {
      m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
    }

    double climber_value = m_controller2.getRightY();
    if (Math.abs(climber_value) > Configuration.controller_dead_zone) {
      m_climber.set(TalonSRXControlMode.PercentOutput, climber_value * Configuration.climber_max_speed);
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

  
  enum AutoStates {
    IDLE,
    SPIN_UP_DELAY,
    PICKUP_BALL,
    DRIVE_TO_BASKET,
    SHOOT_BALLS,
    DRIVE_OUT_AREA,
    STOP
  }

  public void setSparkMaxPIDModes() {
    // PID coefficients
    double kP = 0.1; 
    double kI = 1e-4;
    double kD = 1; 
    double kIz = 0; 
    double kFF = 0; 
    double kMaxOutput = 1; 
    double kMinOutput = -1;

    for (int i = 0; i < 4; i++)
    {
      setSparkMaxMotorPIDValues(i, kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput);
    }
  }

  public void setSparkMaxMotorPIDValues(int index, double kP, double kI, double kD, double kIz, double kFF, double kMax, double kMin) {
    m_pidController[index].setP(kP);
    m_pidController[index].setI(kI);
    m_pidController[index].setD(kD);
    m_pidController[index].setIZone(kIz);
    m_pidController[index].setFF(kFF);
    m_pidController[index].setOutputRange(kMin, kMax);
  }

  @Override
  public void autonomousInit() {
    m_lenc.setPosition(0);
    m_renc.setPosition(0);
    timer.reset();
    timer.start();
    //m_right.set(0);
    setMotorsToBrake(CANSparkMax.IdleMode.kBrake);
    //setSparkMaxPIDModes();
  }

  public void setPositionRef(double posRef) {
    for (int i = 0; i < 4; i++)
    {
      m_pidController[i].setReference(posRef, CANSparkMax.ControlType.kPosition);
    }
  }


  /** This function is called periodically during autonomous. */
  @Override

  public void autonomousPeriodic() {
    System.out.println(m_lenc.getPosition());
    m_harvesterArms.set(Value.kReverse);

    switch (m_autoStates) {
      case IDLE:
        // This is the start of auto mode, shoot ball
        m_launcher.set(Configuration.launcher_high_speed_percentage);
        if (timer.get() > Configuration.launcher_spin_up_delay) {
          m_indexer.set(TalonSRXControlMode.PercentOutput, -Configuration.indexer_max_speed);
        }
        m_lenc.setPosition(0);
        m_renc.setPosition(0);
        if (timer.get() > Configuration.indexer_launch_delay) {
          m_autoStates = AutoStates.PICKUP_BALL;
          m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
        }
        break;
      case PICKUP_BALL:
        // Drive forward 42.7 inches, zero encoders before next state
        m_harvester.set(TalonSRXControlMode.PercentOutput, -Configuration.harvester_max_speed);
        m_harvesterArms.set(Value.kReverse);
        //setPositionRef(-Configuration.auto_move_distance);
        m_myRobot.arcadeDrive(-Configuration.auto_mode_drive_speed, 0);
        if (Math.abs(m_lenc.getPosition()) >= Configuration.auto_move_distance) {
          m_myRobot.arcadeDrive(0, 0);
          m_harvester.set(TalonSRXControlMode.PercentOutput, 0);
          m_harvesterArms.set(Value.kForward);
          m_autoStates = AutoStates.DRIVE_TO_BASKET;
          m_lenc.setPosition(0);
          m_renc.setPosition(0);
        }
        break;
      case DRIVE_TO_BASKET:
        // Drive forward move distance
        m_myRobot.arcadeDrive(Configuration.auto_mode_drive_speed, 0);
        m_launcher.set(Configuration.launcher_high_speed_percentage);
        if (Math.abs(m_lenc.getPosition()) > Configuration.auto_move_distance) {
          m_myRobot.arcadeDrive(0, 0);
          m_currentEncPos = m_lenc.getPosition();
          m_indexer.set(TalonSRXControlMode.PercentOutput, -Configuration.indexer_max_speed);
          m_autoStates = AutoStates.SHOOT_BALLS;
          m_initialTime = timer.get();
        }
        break;
      case SHOOT_BALLS:
        // Start launcher, and start indexer for shoot_basket time variable
        if (timer.get() >= (m_initialTime + Configuration.indexer_launch_delay)) {
          m_autoStates = AutoStates.DRIVE_OUT_AREA;
          m_lenc.setPosition(0);
          m_renc.setPosition(0);
        }
        break;
        
      case DRIVE_OUT_AREA:
        // Drive back out move distance + additional distance
        m_myRobot.arcadeDrive(-Configuration.auto_exit_tarmac_speed, 0);
        if (Math.abs(m_lenc.getPosition()) >= Configuration.auto_move_out_of_tarmac) {
          m_myRobot.arcadeDrive(0, 0);
        } 
        break;
      default:
        // Do nothing, wait until auto period is done
        break;

    }
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
