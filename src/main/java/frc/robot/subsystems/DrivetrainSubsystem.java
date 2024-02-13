// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Configuration;
import frc.robot.Constants;
import frc.robot.LoggedNumber;
import frc.robot.SwerveModule;

import com.ctre.phoenix6.hardware.Pigeon2;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathPlannerPath;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

import java.math.*;

public class DrivetrainSubsystem extends SubsystemBase {
  // https://pathplanner.dev/pplib-build-an-auto.html#create-a-sendablechooser-with-all-autos-in-project

  // Locations for the swerve drive modules relative to the robot center.
  Translation2d m_frontLeftLocation = Configuration.kSwerveTranslations[0];
  Translation2d m_frontRightLocation = Configuration.kSwerveTranslations[1];
  Translation2d m_backLeftLocation = Configuration.kSwerveTranslations[2];
  Translation2d m_backRightLocation = Configuration.kSwerveTranslations[3];

  private final int[] frontLeftChannels = Configuration.kSwerveFLChannels;
  private final int[] frontRightChannels = Configuration.kSwerveFRChannels;
  private final int[] backLeftChannels = Configuration.kSwerveBLChannels;
  private final int[] backRightChannels = Configuration.kSwerveBRChannels;

  private final SwerveModule m_frontLeft = new SwerveModule(
    frontLeftChannels[0], frontLeftChannels[1], frontLeftChannels[2], frontLeftChannels[3]);

  private final SwerveModule m_frontRight = new SwerveModule(
    frontRightChannels[0], frontRightChannels[1], frontRightChannels[2], frontRightChannels[3]);

  private final SwerveModule m_backLeft = new SwerveModule(
    backLeftChannels[0], backLeftChannels[1], backLeftChannels[2], backLeftChannels[3]);

  private final SwerveModule m_backRight = new SwerveModule(
    backRightChannels[0], backRightChannels[1], backRightChannels[2], backRightChannels[3]);
      
  private final SwerveModule m_modules[] = {m_frontLeft, m_frontRight, m_backLeft, m_backRight};
  private final Pigeon2 m_pigeon2 = new Pigeon2(Configuration.kPigeon2Channel);

  // Creating my kinematics object using the module locations
  SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
  m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation
  );

  private final SwerveDriveOdometry m_odometry =
    new SwerveDriveOdometry(
        m_kinematics,
        m_pigeon2.getRotation2d(),
        getModulePositions());

  private Field2d field = new Field2d();

  /** Creates a new DriveSubSystem. */
  public DrivetrainSubsystem() {
    m_pigeon2.reset();

    // https://github.com/mjansen4857/pathplanner/tree/main/examples
    // Configure AutoBuilder
    // AutoBuilder.configureHolonomic(
    //   this::getPose,
    //   this::resetPose,
    //   this::getChassisSpeeds,
    //   this::pathplannerDrive,
    //   Configuration.kPathFollowerConfig,

    //   () -> {
    //       // Boolean supplier that controls when the path will be mirrored for the red alliance
    //       // This will flip the path being followed to the red side of the field.
    //       // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

    //       var alliance = DriverStation.getAlliance();
    //       if (alliance.isPresent()) {
    //           return alliance.get() == DriverStation.Alliance.Red;
    //       }
    //       return false;
    //   },

    //   this);
  }

  public SwerveModule getModule(int offset) {
    return m_modules[offset];
  }

/**
   * Method to drive the robot using joystick info.
   *
   * @param xSpeed Speed of the robot in the x direction (forward).
   * @param ySpeed Speed of the robot in the y direction (sideways).
   * @param rot Angular rate of the robot.
   * @param fieldRelative Whether the provided x and y speeds are relative to the field.
   */
  public void controllerDrive(
      double xSpeed, double ySpeed, double rot, boolean fieldRelative, double periodSeconds) {
    var swerveModuleStates =
        m_kinematics.toSwerveModuleStates(
            ChassisSpeeds.discretize(
                fieldRelative
                    ? ChassisSpeeds.fromFieldRelativeSpeeds(
                        xSpeed, ySpeed, rot, m_pigeon2.getRotation2d())
                    : new ChassisSpeeds(xSpeed, ySpeed, rot),
                periodSeconds));
    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Configuration.kMaxSpeed);

    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_backLeft.setDesiredState(swerveModuleStates[2]);
    m_backRight.setDesiredState(swerveModuleStates[3]);

    updateOdometry();

    // We can log things to the Smartdashboard and to a log file. LoggedNumber is what is called a Singleton
    // SmartDashboard.putNumber("vx", xSpeed);
    // SmartDashboard.putNumber("vy", ySpeed);
    // SmartDashboard.putNumber("omega", rot);
  }

  public void driveFieldSpeeds(ChassisSpeeds fieldSpeeds) {
    var relativeSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
      fieldSpeeds, getPose().getRotation());
    
    driveRobotSpeeds(relativeSpeeds);
  }

  public void driveRobotSpeeds(ChassisSpeeds robotSpeeds) {
    var targetSpeeds = ChassisSpeeds.discretize(robotSpeeds, 0.02);
    var swerveModuleStates = m_kinematics.toSwerveModuleStates(targetSpeeds);

    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Configuration.kMaxSpeed);

    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_backLeft.setDesiredState(swerveModuleStates[2]);
    m_backRight.setDesiredState(swerveModuleStates[3]);

    updateOdometry();
  }

  public SwerveModulePosition[] getModulePositions() {
    return new SwerveModulePosition[] {
      m_frontLeft.getPosition(),
      m_frontRight.getPosition(),
      m_backLeft.getPosition(),
      m_backRight.getPosition()
    };
  }

  public ChassisSpeeds getChassisSpeeds() {
    return m_kinematics.toChassisSpeeds(getModuleStates());
  }

  public SwerveModuleState[] getModuleStates() {
    SwerveModuleState[] states = new SwerveModuleState[m_modules.length];
    for (int i = 0; i < m_modules.length; i++) {
      states[i] = m_modules[i].getState();
    }
    return states;
  }

  /** Updates the field relative position of the robot. */
  public void updateOdometry() {
    m_odometry.update(
        m_pigeon2.getRotation2d(),
        getModulePositions());
  }

  public Pose2d getPose() {
    return m_odometry.getPoseMeters();
  }

  public void resetPose(Pose2d resetPose) {
    m_odometry.resetPosition(m_pigeon2.getRotation2d(), getModulePositions(), resetPose);
  }

  public Command runPath() {
    // // Load the path you want to follow using its name in the GUI
    // PathPlannerPath path = PathPlannerPath.fromPathFile("Test1_Straight");

    // // Create a path following command using AutoBuilder. This will also trigger event markers.
    // return AutoBuilder.followPath(path);
    return new InstantCommand();
  }

  public Command setBrakeModeCmd() {
    // TODO: Set the motors to brake mode
    //    //return new InstantCommand(() -> m_leds.setSpeed(0.61));
    return new InstantCommand();
  }

  public Command setCoastModeCmd() {
    // TODO: Set the motors to coast mode
      return new InstantCommand();
  }

  public Command enableTurbo() {
    // TODO: Enable turbo mode
      

      return new InstantCommand();
  }

  public Command disableTurbo() {
    // TODO: Disable turbo mode
      return new InstantCommand();
  }

  public void setGains(double kp, double ki, double kd, double ks, double kv) {
    m_frontLeft.setGains(kp, ki, kd, ks, kv);
    m_frontRight.setGains(kp, ki, kd, ks, kv);
    m_backLeft.setGains(kp, ki, kd, ks, kv);
    m_backRight.setGains(kp, ki, kd, ks, kv);

    field.setRobotPose(getPose());
    // SmartDashboard.putData("Field", field);
    // SmartDashboard.putNumber("pigeon rotation", m_pigeon2.getAngle());
    // SmartDashboard.putNumber("pose x", getPose().getX());
    // SmartDashboard.putNumber("pose y", getPose().getY());
    // SmartDashboard.putNumber("pose rotation", getPose().getRotation().getDegrees());
  }

  public void homeSwerve() {
    System.out.println("Setting the zero position for the turning motors");
    for (int i = 0; i < 4; i++) {
      m_modules[i].syncSwerveEncoder(Configuration.kZeroPosition[i]);
    }    
  }
}
