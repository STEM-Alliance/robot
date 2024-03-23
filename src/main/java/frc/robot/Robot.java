// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.commands.AimbotCommand;
import frc.robot.commands.AimbotCommand2;
import frc.robot.commands.MoveBotCommand;
import frc.robot.subsystems.*;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  Command m_driveCommand;

  DigitalInput m_noteSensor = new DigitalInput(Configuration.kNoteSensorChannel);

  public DrivetrainSubsystem m_swerve = new DrivetrainSubsystem();
  public IntakeSubsystem m_intake = new IntakeSubsystem(this, m_noteSensor);
  public ShooterSubsystem3 m_shooter = new ShooterSubsystem3();

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

  private SendableChooser<Command> m_autoChooser;
  final AimbotCommand2 m_AimbotCommand = new AimbotCommand2(m_swerve);

  boolean m_previousNoteSensor = true;
  int m_rumbleCounter;
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.startAutomaticCapture();

    // SmartDashboard.putNumber("kp", Configuration.kSwerveKp);
    // SmartDashboard.putNumber("ki", Configuration.kSwerveKi);
    // SmartDashboard.putNumber("kd", Configuration.kSwerveKd);

    // SmartDashboard.putNumber("ks", Configuration.kSwerveKs);
    // SmartDashboard.putNumber("kv", Configuration.kSwerveKv);
    
    m_swerve.homeSwerve();
  
    /**************************************************************
     * Controller 1 
     *************************************************************/
    //final Trigger brake = m_controller1.b();
    // /final Trigger coast = m_controller1.a();
    final Trigger autoAim = m_controller1.b();
    final Trigger enTurbo = m_controller1.rightTrigger();
    final Trigger resetGyro = m_controller1.x();
    final Trigger homeSwerve = m_controller1.y();
    // Do we want the driver to be able to control the setpoints or controller 2 or both
    final Trigger armSetpointUp = m_controller1.povUp();
    final Trigger armSetpointDown = m_controller1.povDown();

    //brake.onTrue(m_swerve.setBrakeModeCmd());
    //coast.onTrue(m_swerve.setCoastModeCmd());
    autoAim.whileTrue(m_AimbotCommand);
    enTurbo.onTrue(m_swerve.enableTurbo());
    enTurbo.onFalse(m_swerve.disableTurbo());
    //runPath.onTrue(m_swerve.runPath());
    resetGyro.onTrue(m_swerve.resetGyro());
    homeSwerve.onTrue(new InstantCommand(() -> m_swerve.homeSwerve()));
    armSetpointUp.onTrue(new InstantCommand(() -> m_shooter.moveSetpointUp()));
    armSetpointDown.onTrue(new InstantCommand(() -> m_shooter.moveSetpointDown()));

    /**************************************************************
     * Controller 2
     *************************************************************/
    final Trigger intakeNote = m_controller2.leftTrigger();
    final Trigger outtakeNote = m_controller2.rightTrigger();
    final Trigger shootSpeaker = m_controller2.y();
    final Trigger shootAmp = m_controller2.x();

    // When you are pressing the intake button, the arm will stay at the lowered position and
    // run the intake until there is a note, the arm will stay down until the button is released
    intakeNote.whileTrue(m_shooter.setSetpoint(0).andThen(
      m_intake.fwdIntake(false)));
    intakeNote.onFalse(m_shooter.setSetpoint(1));

    outtakeNote.whileTrue(m_intake.revIntake());
    outtakeNote.onFalse(m_intake.stopIntake());

    // When you are pressing the shoot speaker button, the shooter will spin up to velocity and
    // move the note into the shooter
    // (Make aimbot for the speaker run automatically? or run manually)
    shootSpeaker.whileTrue((m_shooter.spinShooterToVelocity().andThen(
      m_intake.fwdIntake(true))));
    shootSpeaker.onFalse(m_shooter.stopShooter());

    // When you press the button, the shooter will just move to the setpoint for the amp
    // When the arm is up, you can line up and then hold the button, which will run the intake
    // (Make aimbot for amp run automatically? or run manually)
    shootAmp.whileTrue(m_shooter.setSetpoint(2).andThen(
      m_shooter.atSetpoint().andThen(
      m_intake.revIntake())));
    shootAmp.onFalse(m_intake.stopIntake());

    // final Trigger fwdIntake = m_controller2.a();
    // final Trigger autoAim = m_controller2.b();
    // final Trigger shoot = m_controller2.y();
    // final Trigger shootAmp = m_controller2.rightBumper();
    // final Trigger ampPos = m_controller2.leftBumper();
    // final Trigger engageClimbBrake = m_controller2.x();
    // final Trigger climbUp = m_controller2.rightTrigger();
    // final Trigger climbDown = m_controller2.leftTrigger();
    // final Trigger up = m_controller2.pov(0);
    // final Trigger down = m_controller2.pov(180);
    // final Trigger left = m_controller2.pov(270);
    // final Trigger right = m_controller2.pov(90);

    // engageClimbBrake.onTrue(m_climber.toggleClimbBrakeCmd());

    //Get the default instance of NetworkTables that was created automatically
    //when your program starts
    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    // Attempting to get the driver station position
    NetworkTable fmsInfo = inst.getTable("FMSInfo");
    NetworkTableEntry m_driverStationPos = fmsInfo.getEntry("StationNumber");
    Number pos = m_driverStationPos.getNumber(0);

    System.out.println("Driver Station number: " + pos.toString());
    System.out.println("Robot starting");

    m_autoChooser = AutoBuilder.buildAutoChooser(); // Default auto will be `Commands.none()`
    SmartDashboard.putData("Auto Mode", m_autoChooser);
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

    // if (m_controller2.getRightTriggerAxis() > 0.1)
    // {
    //   m_climber.runClimber(m_controller2.getRightTriggerAxis());
    // }
    // else if (m_controller2.getLeftTriggerAxis() > 0.1)
    // {
    //   m_climber.runClimber(-m_controller2.getLeftTriggerAxis());
    // }
    // else 
    // {
    //   m_climber.runClimber(0);
    // }

    // if (m_intake.m_noteSensor.get()) {
    //   m_leds.setRed();
    // }
    // else {
    //   m_leds.setFlashingGreen();
    // }

    // m_shooter.movementLoop();
    // Uncomment this line to print the motor positions.
    m_swerve.printHomePos();

    boolean notesensor = m_noteSensor.get();
    if (notesensor & !m_previousNoteSensor)
    {
      m_rumbleCounter = 0;
    }
    m_previousNoteSensor = notesensor;
    if (m_rumbleCounter < Configuration.KRumbleTimer)
    {
      m_controller1.getHID().setRumble(RumbleType.kBothRumble, 1.0);
      m_controller2.getHID().setRumble(RumbleType.kBothRumble, 1.0);
      m_rumbleCounter++;
    }
    else {
      m_controller1.getHID().setRumble(RumbleType.kBothRumble, 0);
      m_controller2.getHID().setRumble(RumbleType.kBothRumble, 0);
    }

  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {

    m_autonomousCommand = m_autoChooser.getSelected();

    // switch (m_autoSelected)
    // {
    //   case kShoot:
    //     // Put custom auto code here
    //     m_autonomousCommand = getUnhookAndShoot();
    //     System.out.println("Shoot");
    //     break;
    //   case kShootAndScoot:
    //     m_autonomousCommand = getUnhookAndShoot().andThen(
    //       Commands.parallel(new MoveBotCommand(m_swerve),
    //       m_intake.fwdIntake(false)));
    //     break;
    //   case kDriveStraight:
    //     m_autonomousCommand = new PathPlannerAuto("Auto1");
    //     break;
    //   case kLoop:
    //     m_autonomousCommand = new PathPlannerAuto("GoLong");
    // }

    // Overwrite the current heading with what the limelight sees
    //m_swerve.setGyro(m_limelight.getHeading());

    // Reset the robot pose to what the limelight sees
    //double[] visionMeasurements = m_limelight.getBotPose();
    //m_swerve.resetPose(new Pose2d(visionMeasurements[0], visionMeasurements[1],
    //  m_swerve.getPose().getRotation()));

    //m_swerve.resetPose(new Pose2d(14.5, 1.5, new Rotation2d(0)));
    //m_swerve.setGyro(0);

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  private Command PathPlannerAuto(String string) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'PathPlannerAuto'");
  }

  // public Command getUnhookAndShoot() {
  //   return m_shooter.unhookShooter().andThen(
  //          m_shooter.lowerShooter().andThen(
  //          m_shooter.spinShooterToVelocity().andThen(
  //          m_intake.fwdIntakeTimed().andThen(
  //          new WaitCommand(2).andThen(
  //          m_shooter.stopShooter().andThen(
  //         m_intake.stopIntake()))))));
  // }

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
    var expLeftX = exponentialScaling(leftX, Configuration.kExpControl);
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

    rot += m_AimbotCommand.getDesiredRotation();

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
    final var ySpeed = MathUtil.applyDeadband(-expLeftY, Math.pow(
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