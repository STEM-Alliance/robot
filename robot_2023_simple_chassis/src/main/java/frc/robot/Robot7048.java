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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.networktables.*;
import java.lang.Math;
import edu.wpi.first.wpilibj.Timer;
//import frc.robot.DualTalonSRX;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

/**
 * This is a demo program showing the use of the DifferentialDrive class,
 * specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot7048 extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private XboxController m_controller1;

  private final DualTalonSRX m_leftMotor = new DualTalonSRX(1, 2, TalonSRXControlMode.PercentOutput, true);
  private final DualTalonSRX m_rightMotor = new DualTalonSRX(10, 11, TalonSRXControlMode.PercentOutput, false);

  SlewRateLimiter m_forwardFilter = new SlewRateLimiter(Configuration.forward_back_slew_rate);
  SlewRateLimiter m_rotationFilter = new SlewRateLimiter(Configuration.right_left_slew_rate);
  Timer timer = new Timer();

  //private AutoStates m_autoStates = AutoStates.IDLE;
  private double m_currentEncPos;
  private double m_initialTime;

  DoubleSubscriber pub;
  DoubleSubscriber sub;

  // public void setMotorsToBrake(IdleMode mode)
  // {
  //   m_leftMotor1.setIdleMode(mode);
  //   m_leftMotor2.setIdleMode(mode);
  //   m_rightMotor1.setIdleMode(mode);
  //   m_rightMotor2.setIdleMode(mode);
  // }

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    //CameraServer.startAutomaticCapture();
    //m_right.setInverted(true);
    //setMotorsToBrake(CANSparkMax.IdleMode.kCoast);
    //m_climber.setNeutralMode(NeutralMode.Brake);

    m_myRobot = new DifferentialDrive(m_leftMotor, m_rightMotor);
    m_myRobot.setDeadband(0.15);
    m_controller1 = new XboxController(0);
    //m_controller2 = new XboxController(1);

    // m_lenc.setPositionConversionFactor(Configuration.encoder_counts_to_inches);
    // m_renc.setPositionConversionFactor(Configuration.encoder_counts_to_inches);
    // m_lenc.setPosition(0);
    // m_renc.setPosition(0);

    // TalonSRXConfiguration config = new TalonSRXConfiguration();
    // config.peakCurrentLimit = 40; // the peak current, in amps
    // config.peakCurrentDuration = 1500; // the time at the peak current before the limit triggers, in ms
    // config.continuousCurrentLimit = 30; // the current to maintain if the peak limit is triggered
    // m_harvester.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    // m_indexer.configAllSettings(config); // apply the config settings; this selects the quadrature encoder
    // m_climber.configAllSettings(config);

    // m_pidController[0] = m_leftMotor1.getPIDController();
    // m_pidController[1] = m_leftMotor2.getPIDController();
    // m_pidController[2] = m_rightMotor1.getPIDController();
    // m_pidController[3] = m_rightMotor2.getPIDController();

    System.out.println("Robot starting");

    // m_leftMotor.updateControlMode(TalonSRXControlMode.Position);
    // m_rightMotor.updateControlMode(TalonSRXControlMode.Position);
    // m_leftMotor.set(12);
    // m_rightMotor.set(12);
  
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    sub = inst.getDoubleTopic("/datatable/X").subscribe(0);
  }

  @Override
  public void teleopPeriodic() {
    // Controller 1 will control the robot movement, the arms, and the harvester
    //setMotorsToBrake(CANSparkMax.IdleMode.kCoast);
    // The rotation needs to be inverted. Otherwise the robot will turn in the wrong
    // direction
    double multiFactor = Configuration.fine_controller_derate;
    // if (m_controller1.getYButton()) {
    //   multiFactor = 1;
    // }

    double y = m_controller1.getLeftY();
    double x = m_controller1.getLeftX();
    double fy = m_forwardFilter.calculate(y * multiFactor);
    double fx = m_rotationFilter.calculate(x * multiFactor);

    m_myRobot.arcadeDrive(fy, fx);

    SmartDashboard.putNumber("Quad Counts Left", m_leftMotor.getSensors(0).getQuadraturePosition());
    SmartDashboard.putNumber("Quad Counts Right", m_rightMotor.getSensors(1).getQuadraturePosition());
    SmartDashboard.putNumber("x", x);
    SmartDashboard.putNumber("y", y);
    SmartDashboard.putNumber("fx", fx);
    SmartDashboard.putNumber("fy", fy);
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

  
  // enum AutoStates {
  //   IDLE,
  //   SPIN_UP_DELAY,
  //   PICKUP_BALL,
  //   DRIVE_TO_BASKET,
  //   SHOOT_BALLS,
  //   DRIVE_OUT_AREA,
  //   STOP
  // }

  // public void setSparkMaxPIDModes() {
  //   // PID coefficients
  //   double kP = 0.1; 
  //   double kI = 1e-4;
  //   double kD = 1; 
  //   double kIz = 0; 
  //   double kFF = 0; 
  //   double kMaxOutput = 1; 
  //   double kMinOutput = -1;

  //   for (int i = 0; i < 4; i++)
  //   {
  //     setSparkMaxMotorPIDValues(i, kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput);
  //   }
  // }

  // public void setSparkMaxMotorPIDValues(int index, double kP, double kI, double kD, double kIz, double kFF, double kMax, double kMin) {
  //   m_pidController[index].setP(kP);
  //   m_pidController[index].setI(kI);
  //   m_pidController[index].setD(kD);
  //   m_pidController[index].setIZone(kIz);
  //   m_pidController[index].setFF(kFF);
  //   m_pidController[index].setOutputRange(kMin, kMax);
  // }

  @Override
  public void autonomousInit() {
    //m_leftMotor.updateControlMode(TalonSRXControlMode.Position);
    //m_rightMotor.updateControlMode(TalonSRXControlMode.Position);
  }

  // public void setPositionRef(double posRef) {
  //   for (int i = 0; i < 4; i++)
  //   {
  //     m_pidController[i].setReference(posRef, CANSparkMax.ControlType.kPosition);
  //   }
  // }


  // /** This function is called periodically during autonomous. */
  @Override

  public void autonomousPeriodic() {
  
    // m_leftMotor.set(25000);
    // m_rightMotor.set(25000);
  }
    //   System.out.println(m_lenc.getPosition());
  //   m_harvesterArms.set(Value.kReverse);

  //   switch (m_autoStates) {
  //     case IDLE:
  //       // This is the start of auto mode, shoot ball
  //       m_launcher.set(Configuration.launcher_high_speed_percentage);
  //       if (timer.get() > Configuration.launcher_spin_up_delay) {
  //         m_indexer.set(TalonSRXControlMode.PercentOutput, -Configuration.indexer_max_speed);
  //       }
  //       m_lenc.setPosition(0);
  //       m_renc.setPosition(0);
  //       if (timer.get() > Configuration.indexer_launch_delay) {
  //         m_autoStates = AutoStates.PICKUP_BALL;
  //         m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
  //       }
  //       break;
  //     case PICKUP_BALL:
  //       // Drive forward 42.7 inches, zero encoders before next state
  //       m_harvester.set(TalonSRXControlMode.PercentOutput, -Configuration.harvester_max_speed);
  //       m_harvesterArms.set(Value.kReverse);
  //       //setPositionRef(-Configuration.auto_move_distance);
  //       m_myRobot.arcadeDrive(-Configuration.auto_mode_drive_speed, 0);
  //       if (Math.abs(m_lenc.getPosition()) >= Configuration.auto_move_distance) {
  //         m_myRobot.arcadeDrive(0, 0);
  //         m_harvester.set(TalonSRXControlMode.PercentOutput, 0);
  //         m_harvesterArms.set(Value.kForward);
  //         m_autoStates = AutoStates.DRIVE_TO_BASKET;
  //         m_lenc.setPosition(0);
  //         m_renc.setPosition(0);
  //       }
  //       break;
  //     case DRIVE_TO_BASKET:
  //       // Drive forward move distance
  //       m_myRobot.arcadeDrive(Configuration.auto_mode_drive_speed, 0);
  //       m_launcher.set(Configuration.launcher_high_speed_percentage);
  //       if (Math.abs(m_lenc.getPosition()) > Configuration.auto_move_distance) {
  //         m_myRobot.arcadeDrive(0, 0);
  //         m_currentEncPos = m_lenc.getPosition();
  //         m_indexer.set(TalonSRXControlMode.PercentOutput, -Configuration.indexer_max_speed);
  //         m_autoStates = AutoStates.SHOOT_BALLS;
  //         m_initialTime = timer.get();
  //       }
  //       break;
  //     case SHOOT_BALLS:
  //       // Start launcher, and start indexer for shoot_basket time variable
  //       if (timer.get() >= (m_initialTime + Configuration.indexer_launch_delay)) {
  //         m_autoStates = AutoStates.DRIVE_OUT_AREA;
  //         m_lenc.setPosition(0);
  //         m_renc.setPosition(0);
  //       }
  //       break;
        
  //     case DRIVE_OUT_AREA:
  //       // Drive back out move distance + additional distance
  //       m_myRobot.arcadeDrive(-Configuration.auto_exit_tarmac_speed, 0);
  //       if (Math.abs(m_lenc.getPosition()) >= Configuration.auto_move_out_of_tarmac) {
  //         m_myRobot.arcadeDrive(0, 0);
  //       } 
  //       break;
  //     default:
  //       // Do nothing, wait until auto period is done
  //       break;

  //   }
  // }
}
