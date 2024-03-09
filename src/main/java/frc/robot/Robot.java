// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.nio.file.Path;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.commands.AimbotCommand;
import frc.robot.commands.ClimbingCommand;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.IntakeCommand;
import frc.robot.commands.MoveBotCommand;
import frc.robot.commands.ShootCommand;
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
 
  boolean m_enableDrive = true;
  boolean m_reverseMidtake = false;
  PathPlannerPath Path = PathPlannerPath.fromPathFile("Example Path");
  DrivetrainSubsystem m_swerve = new DrivetrainSubsystem();
  IntakeSubSystem m_intake = new IntakeSubSystem(9, 11, 10);
  ClimbingSystem m_Climber = new ClimbingSystem(13, 14);
  public CommandXboxController m_controller1 = new CommandXboxController(0);
  CommandXboxController m_controller2 = new CommandXboxController(1);

  LEDSubsystem m_leds = new LEDSubsystem();

  final SendableChooser<String> m_kp = new SendableChooser<>();
  final SendableChooser<String> m_ki = new SendableChooser<>();
  final SendableChooser<String> m_kd = new SendableChooser<>();


  Command m_aimbotCommand = new AimbotCommand(m_swerve);
  Command m_driveCommand = new DriveCommand(m_swerve, false, m_controller1);
  Command m_IntakeCommand = new IntakeCommand(m_intake, m_reverseMidtake);
  Command m_ShootCommand = new ShootCommand(m_intake);
  Command m_ClimbingCommand = new ClimbingCommand(m_Climber);
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    NamedCommands.registerCommand("intake", m_IntakeCommand);
    
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
    // Controller 1
    final Trigger enableDrive = m_controller1.b();
    final Trigger shoot = m_controller1.rightTrigger();
    final Trigger autoAim = m_controller1.a();
    //final Trigger Drive = m_controller1.rightBumper();
    final Trigger homeSwerve = m_controller1.y();
    // final Trigger toggleHDrive = m_controller1.rightBumper();
    // toggleHDrive.onTrue(m_pneumatics.toggleHDrive());
  
    
    // Controller 2
    final Trigger reverseMidtake = m_controller1.b();
    // final Trigger gripper_control = m_controller2.leftTrigger();
    // final Trigger extend_control = m_controller2.rightTrigger();
    // final Trigger high = m_controller2.y();
    // final Trigger medium = m_controller2.b();
    // final Trigger low = m_controller2.a();
    //final Trigger leftBumper = m_controller2.leftBumper();
    // final Trigger rightBumper = m_controller2.rightBumper();
    final Trigger Climb = m_controller2.x();
    final Trigger leftTrigger = m_controller2.axisGreaterThan(XboxController.Axis.kLeftTrigger.value, 0.5);
    final Trigger rightTrigger = m_controller2.axisGreaterThan(XboxController.Axis.kRightTrigger.value, 0.5);
    // final Trigger retractHome = m_controller2.x();
    // final Trigger up = m_controller2.pov(0);
    // final Trigger down = m_controller2.pov(180);
    // final Trigger left = m_controller2.pov(270);
    // final Trigger right = m_controller2.pov(90);

    
    // up.onTrue(m_leds.red());
    // left.onTrue(m_leds.yellow());
    // right.onTrue(m_leds.blue());
    // down.onTrue(m_leds.crazy());
    homeSwerve.onTrue(new InstantCommand(() -> m_swerve.homeSwerve()));
   
    //brake.onTrue(m_swerve.setBrakeModeCmd());
    
    Climb.whileTrue(m_ClimbingCommand);
    enableDrive.onTrue(m_driveCommand);
    autoAim.onTrue(m_swerve.setBrakeModeCmd().andThen(m_aimbotCommand));
    //autoAim.onTrue(m_swerve.setBrakeModeCmd().andThen(m_aimbotCommand.andThen(m_driveCommand)));
    leftTrigger.whileTrue(m_IntakeCommand);
    rightTrigger.whileTrue(m_ShootCommand);
    reverseMidtake.whileTrue(m_ShootCommand);
    
    if (enableDrive.getAsBoolean()) 
    {
      m_enableDrive = true;
    }
   
    if (reverseMidtake.getAsBoolean()) 
    {
      m_reverseMidtake = true;
    }
    else 
    {
      m_reverseMidtake = false;
    }
    // leftTrigger.whileFalse(new InstantCommand(() -> m_intake.doneLoading()));
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

    m_swerve.m_ahrs.zeroYaw();
    m_swerve.homeSwerve();
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
    SmartDashboard.putNumber("Yaw", m_swerve.m_ahrs.getYaw());
    double kp = SmartDashboard.getNumber("kp", Configuration.kDriveKp);
    double ki = SmartDashboard.getNumber("ki", Configuration.kDriveKi);
    double kd = SmartDashboard.getNumber("kd", Configuration.kDriveKd);
    m_swerve.setGains(kp, ki, kd);
    //m_swerve.printAbsPos();
}

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_swerve.homeSwerve();
    m_swerve.m_ahrs.zeroYaw();
    m_autonomousCommand = new MoveBotCommand(m_swerve);
    // m_autonomousCommand = new InstantCommand(() -> m_swerve.drive(1.5, 0, 0, false, 0.02)).andThen(
    //                       new WaitCommand(2).andThen(
    //                       new InstantCommand(() -> m_swerve.drive(0, 0, 0, false, 0.02))));
    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
    
      
      
      // AutoBuilder.buildAuto("basic");
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // AutoBuilder.followPath(Path);
  }
  
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
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() 
  {
    
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
