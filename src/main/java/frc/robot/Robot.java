// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.commands.AimbotCommand;
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

  public DrivetrainSubsystem m_swerve = new DrivetrainSubsystem();
  public IntakeSubsystem m_intake = new IntakeSubsystem(this);
  public ShooterSubsystem2 m_shooter = new ShooterSubsystem2();

  public LimelightSubsystem m_limelight = new LimelightSubsystem(m_swerve);

  CommandXboxController m_controller1 = new CommandXboxController(0);
  CommandXboxController m_controller2 = new CommandXboxController(1);

  // Slew rate limiters to make joystick inputs more gentle; 1/3 sec from 0 to 1.
  private final SlewRateLimiter m_xspeedLimiter = new SlewRateLimiter(Configuration.kVxSlewRateLimit);
  private final SlewRateLimiter m_yspeedLimiter = new SlewRateLimiter(Configuration.kVySlewRateLimit);
  private final SlewRateLimiter m_rotLimiter = new SlewRateLimiter(Configuration.kOmegaSlewRateLimit);

  LEDSubsystem m_leds = new LEDSubsystem();

  ClimberSubsystem m_climber = new ClimberSubsystem(Configuration.kClimbMotorCanID);
  Command m_climbUp;
  Command m_climbDown;

  final String kShoot = "Shoot";
  final String kShootAndScoot = "ShootAndScoot";
  String m_autoSelected;
  final SendableChooser<String> m_autoChooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.startAutomaticCapture();

    SmartDashboard.putNumber("kp", Configuration.kSwerveKp);
    SmartDashboard.putNumber("ki", Configuration.kSwerveKi);
    SmartDashboard.putNumber("kd", Configuration.kSwerveKd);

    SmartDashboard.putNumber("ks", Configuration.kSwerveKs);
    SmartDashboard.putNumber("kv", Configuration.kSwerveKv);
    
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    //m_shooter.register();
    m_swerve.homeSwerve();
    // m_swerve.register();
  
    /**************************************************************
     * Controller 1 
     *************************************************************/
    final Trigger brake = m_controller1.b();
    final Trigger coast = m_controller1.a();
    final Trigger enTurbo = m_controller1.rightTrigger();
    final Trigger resetGyro = m_controller1.x();
    final Trigger homeSwerve = m_controller1.y();

    final Trigger raiseArm = m_controller1.rightTrigger();
    final Trigger lowerArm = m_controller1.leftTrigger();
    final Trigger armPositionUp = m_controller1.povUp();
    final Trigger armPositionDown = m_controller1.povDown();

    /**************************************************************
     * Controller 2
     *************************************************************/
    final Trigger fwdIntake = m_controller2.a();
    final Trigger autoAim = m_controller2.b();
    final Trigger shoot = m_controller2.y();
    final Trigger shootAmp = m_controller2.rightBumper();
    final Trigger ampPos = m_controller2.leftBumper();
    final Trigger engageClimbBrake = m_controller2.x();
    final Trigger climbUp = m_controller2.rightTrigger();
    final Trigger climbDown = m_controller2.leftTrigger();
    final Trigger up = m_controller2.pov(0);
    final Trigger down = m_controller2.pov(180);
    final Trigger left = m_controller2.pov(270);
    final Trigger right = m_controller2.pov(90);
    
    
    up.onTrue(m_leds.red());
    left.onTrue(m_leds.yellow());
    right.onTrue(m_leds.blue());
    down.onTrue(m_leds.crazy());
    brake.onTrue(m_swerve.setBrakeModeCmd());
    coast.onTrue(m_swerve.setCoastModeCmd());
    enTurbo.onTrue(m_swerve.enableTurbo());
    enTurbo.onFalse(m_swerve.disableTurbo());
    engageClimbBrake.onTrue(m_climber.toggleClimbBrakeCmd());
    autoAim.whileTrue(new AimbotCommand(m_swerve));
    ampPos.onTrue(m_shooter.ampPosition());

    //runPath.onTrue(m_swerve.runPath());
    resetGyro.onTrue(m_swerve.resetGyro());
    homeSwerve.onTrue(new InstantCommand(() -> m_swerve.homeSwerve()));
    m_swerve.homeSwerve();
    
    fwdIntake.onTrue(m_intake.fwdIntake(false));
    shoot.whileTrue((m_shooter.spinShooterToVelocity().andThen(
      m_intake.fwdIntake(true)
      )));

    shootAmp.whileTrue((m_shooter.spinShooterToVelocity(-0.4).andThen(
      m_intake.fwdIntake(true))));

    shoot.onFalse(m_shooter.stopShooter());
    shootAmp.onFalse(m_shooter.stopShooter());

    //Get the default instance of NetworkTables that was created automatically
    //when your program starts
    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    // Attempting to get the driver station position
    NetworkTable fmsInfo = inst.getTable("FMSInfo");
    NetworkTableEntry m_driverStationPos = fmsInfo.getEntry("StationNumber");
    Number pos = m_driverStationPos.getNumber(0);

    System.out.println("Driver Station number: " + pos.toString());
    System.out.println("Robot starting");

    m_autoChooser.setDefaultOption(kShoot, kShoot);
    m_autoChooser.addOption(kShootAndScoot, kShootAndScoot);
    SmartDashboard.putData("Auto choices", m_autoChooser);

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

    if (m_controller2.getRightTriggerAxis() > 0.1)
    {
      m_climber.runClimber(m_controller2.getRightTriggerAxis());
    }
    else if (m_controller2.getLeftTriggerAxis() > 0.1)
    {
      m_climber.runClimber(-m_controller2.getLeftTriggerAxis());
    }
    else 
    {
      m_climber.runClimber(0);
    }

    if (m_intake.m_noteSensor.get()) {
      m_leds.setBlue();
    }
    else {
      m_leds.setRed();
    }

    // m_shooter.movementLoop();
    // Uncomment this line to print the motor positions.
    m_swerve.printHomePos();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {

    m_autoSelected = m_autoChooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
    switch (m_autoSelected)
    {
      case kShoot:
        // Put custom auto code here
        m_autonomousCommand = getUnhookAndShoot();
        System.out.println("Shoot");
        break;
      case kShootAndScoot:
        m_autonomousCommand = getUnhookAndShoot().andThen(
                              new WaitCommand(2).andThen(
                              new InstantCommand(() -> m_swerve.controllerDrive(1.5, 0, 0, false, 0.2)).andThen(
                              new WaitCommand(2).andThen(
                              new InstantCommand(() -> m_swerve.controllerDrive(0, 0, 0, false, 0.2))))));
        break;
    }

    // Overwrite the current heading with what the limelight sees
    m_swerve.setGyro(m_limelight.getHeading());

    // Reset the robot pose to what the limelight sees
    double[] visionMeasurements = m_limelight.getBotPose();
    m_swerve.resetPose(new Pose2d(visionMeasurements[0], visionMeasurements[1],
      m_swerve.getPose().getRotation()));

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  public Command getUnhookAndShoot() {
    return m_shooter.unhookShooter().andThen(
           m_shooter.lowerShooter().andThen(
           m_shooter.spinShooterToVelocity().andThen(
           m_intake.fwdIntakeTimed().andThen(
           new WaitCommand(2).andThen(
           m_shooter.stopShooter().andThen(
          m_intake.stopIntake()))))));
  }

  public Command getAutonomousCommand() {
    return new PathPlannerAuto("Auto1");
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // unhookShooter, then lowerShooter, then shoot until 5 seconds passed
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.

    // angle arm not called during this period

    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    driveWithJoystick(true);
    moveArm();
    cmdIntake();
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
    var leftX = m_controller1.getLeftX();
    var leftY = m_controller1.getLeftY();
    var rightX = m_controller1.getRightX();
    var expLeftX = exponentialScaling(leftX, Configuration.kExpControl); //getHID()
    var expLeftY = exponentialScaling(leftY, Configuration.kExpControl);
    var expOmega = exponentialScaling(rightX, Configuration.kExpControl);

    // Get the x speed. We are inverting this because Xbox controllers return
    // negative values when we push forward.
    final var xSpeed =
        m_xspeedLimiter.calculate(MathUtil.applyDeadband(expLeftY, Math.pow(
          Configuration.GeneralDeadband, Configuration.kExpControl))) * Configuration.kMaxSpeed;

    // Get the y speed or sideways/strafe speed. We are inverting this because
    // we want a positive value when we pull to the left. Xbox controllers
    // return positive values when you pull to the right by default.
    final var ySpeed =
        m_yspeedLimiter.calculate(MathUtil.applyDeadband(expLeftX, Math.pow(
          Configuration.GeneralDeadband, Configuration.kExpControl))) * Configuration.kMaxSpeed;

    // Get the rate of angular rotation. We are inverting this because we want a
    // positive value when we pull to the left (remember, CCW is positive in
    // mathematics). Xbox controllers return positive values when you pull to
    // the right by default.
    var rot = m_rotLimiter.calculate(MathUtil.applyDeadband(expOmega, Math.pow(
      Configuration.GeneralDeadband, Configuration.kExpControl))) * Configuration.kMaxAngularSpeed;

    LoggedNumber.getInstance().logNumber("rot_prelimit", rot);

    // Limit rotation to 1 - the driving omega factor when driving any direction
    rot = rot * (1 - (Configuration.kDrivingOmegaFactor *
      (Math.abs(expLeftX) + Math.abs(expLeftY))));
    
    m_swerve.controllerDrive(xSpeed, ySpeed, rot, fieldRelative, getPeriod());
  
    LoggedNumber.getInstance().logNumber("LeftX", leftX);
    LoggedNumber.getInstance().logNumber("LeftY", leftY);
    LoggedNumber.getInstance().logNumber("RightX", rightX);
    LoggedNumber.getInstance().logNumber("expLeftX", expLeftX);
    LoggedNumber.getInstance().logNumber("expLeftY", expLeftY);
    LoggedNumber.getInstance().logNumber("expOmega", expOmega);
  
  }

  private void moveArm() {
    var expLeftY = exponentialScaling(m_controller2.getLeftY(), Configuration.kExpControl);

    // Get the y speed or sideways/strafe speed. We are inverting this because
    // we want a positive value when we pull to the left. Xbox controllers
    // return positive values when you pull to the right by default.
    final var ySpeed = MathUtil.applyDeadband(expLeftY, Math.pow(
          Configuration.GeneralDeadband, Configuration.kExpControl));

    m_shooter.setPosition(ySpeed);
  }

  private void cmdIntake() {
    var expRightY = exponentialScaling(m_controller2.getRightY(), Configuration.kExpControl);

    final var ySpeed = MathUtil.applyDeadband(expRightY, Math.pow(
          Configuration.GeneralDeadband, Configuration.kExpControl));

    m_intake.cmdIntake(ySpeed);
  }

  public double exponentialScaling(double base, double exponent) {
    if (base > 0) {
      return Math.pow(Math.abs(base), exponent);
    }

    return -Math.pow(Math.abs(base), exponent);
  }

}