// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.HomeSwerve;
import frc.robot.subsystems.*;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  Command m_driveCommand;
  DrivetrainSubsystem m_swerve = new DrivetrainSubsystem();
  //IntakeSubSystem m_intake = new IntakeSubSystem(10, 11);
  CommandXboxController m_controller1 = new CommandXboxController(0);
  //CommandXboxController m_controller2 = new CommandXboxController(1);

  // Slew rate limiters to make joystick inputs more gentle; 1/3 sec from 0 to 1.
  private final SlewRateLimiter m_xspeedLimiter = new SlewRateLimiter(Configuration.kVxSlewRateLimit);
  private final SlewRateLimiter m_yspeedLimiter = new SlewRateLimiter(Configuration.kVySlewRateLimit);
  private final SlewRateLimiter m_rotLimiter = new SlewRateLimiter(Configuration.kOmegaSlewRateLimit);

  LEDSubsystem m_leds = new LEDSubsystem();

  final SendableChooser<String> m_kp = new SendableChooser<>();
  final SendableChooser<String> m_ki = new SendableChooser<>();
  final SendableChooser<String> m_kd = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
    
    // Controller 1
    final Trigger brake = m_controller1.b();
    final Trigger coast = m_controller1.a();
    final Trigger enTurbo = m_controller1.rightTrigger();
    final Trigger disTurbo = m_controller1.x();
    final Trigger homeSwerve = m_controller1.y();
    // final Trigger toggleHDrive = m_controller1.rightBumper();
    // toggleHDrive.onTrue(m_pneumatics.toggleHDrive());
  
    // Controller 2
    // final Trigger gripper_control = m_controller2.leftTrigger();
    // final Trigger extend_control = m_controller2.rightTrigger();
    // final Trigger high = m_controller2.y();
    // final Trigger medium = m_controller2.b();
    // final Trigger low = m_controller2.a();
    // final Trigger leftBumper = m_controller2.leftBumper();
    // final Trigger rightBumper = m_controller2.rightBumper();
    // final Trigger leftTrigger = m_controller2.axisGreaterThan(XboxController.Axis.kLeftTrigger.value, 0.5);
    // final Trigger rightTrigger = m_controller2.axisGreaterThan(XboxController.Axis.kRightTrigger.value, 0.5);
    // final Trigger retractHome = m_controller2.x();
    // final Trigger up = m_controller2.pov(0);
    // final Trigger down = m_controller2.pov(180);
    // final Trigger left = m_controller2.pov(270);
    // final Trigger right = m_controller2.pov(90);

    
    
    // up.onTrue(m_leds.red());
    // left.onTrue(m_leds.yellow());
    // right.onTrue(m_leds.blue());
    // down.onTrue(m_leds.crazy());
    homeSwerve.onTrue(new HomeSwerve(m_swerve));
    brake.onTrue(m_swerve.setBrakeModeCmd());
    coast.onTrue(m_swerve.setCoastModeCmd());
    enTurbo.onTrue(m_swerve.enableTurbo());
    enTurbo.onFalse(m_swerve.disableTurbo());
    
    //leftTrigger.onTrue(m_intake.grabNote());
    //rightTrigger.onTrue(m_intake.shootNote());

    //Get the default instance of NetworkTables that was created automatically
    //when your program starts
    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    // Attempting to get the driver station position
    NetworkTable fmsInfo = inst.getTable("FMSInfo");
    NetworkTableEntry m_driverStationPos = fmsInfo.getEntry("StationNumber");
    Number pos = m_driverStationPos.getNumber(0);

    System.out.println("Driver Station number: " + pos.toString());
    System.out.println("Robot starting");
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();

    driveWithJoystick(true);
    double kp = SmartDashboard.getNumber("kp", Configuration.kDriveKp);
    double ki = SmartDashboard.getNumber("ki", Configuration.kDriveKi);
    double kd = SmartDashboard.getNumber("kd", Configuration.kDriveKd);
    m_swerve.setGains(kp, ki, kd);
}

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    //driveWithJoystick(true);
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}

  private void driveWithJoystick(boolean fieldRelative) {
    // Get the x speed. We are inverting this because Xbox controllers return
    // negative values when we push forward.
    final var xSpeed =
        -m_xspeedLimiter.calculate(MathUtil.applyDeadband(m_controller1.getLeftY(), Configuration.GeneralDeadband))
            * Configuration.kMaxSpeed;

    // Get the y speed or sideways/strafe speed. We are inverting this because
    // we want a positive value when we pull to the left. Xbox controllers
    // return positive values when you pull to the right by default.
    final var ySpeed =
        -m_yspeedLimiter.calculate(MathUtil.applyDeadband(m_controller1.getLeftX(), Configuration.GeneralDeadband))
            * Configuration.kMaxSpeed;

    // Get the rate of angular rotation. We are inverting this because we want a
    // positive value when we pull to the left (remember, CCW is positive in
    // mathematics). Xbox controllers return positive values when you pull to
    // the right by default.
    final var rot =
        -m_rotLimiter.calculate(MathUtil.applyDeadband(m_controller1.getRightX(), Configuration.GeneralDeadband))
            * Configuration.kMaxAngularSpeed;

    m_swerve.drive(xSpeed, ySpeed, rot, fieldRelative, getPeriod());
  }

}
