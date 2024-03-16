// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Configuration;
import frc.robot.LoggedNumber;
import frc.robot.SwerveModule;

import com.ctre.phoenix6.hardware.Pigeon2;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.util.PathPlannerLogging;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

import java.math.*;

public class DrivetrainSubsystem extends SubsystemBase {
  // https://pathplanner.dev/pplib-build-an-auto.html#create-a-sendablechooser-with-all-autos-in-project

  // Locations for the swerve drive modules relative to the robot center.
  Translation2d m_frontLeftLocation = Configuration.kSwerveTranslations[0];
  Translation2d m_frontRightLocation = Configuration.kSwerveTranslations[1];
  Translation2d m_backLeftLocation = Configuration.kSwerveTranslations[2];
  Translation2d m_backRightLocation = Configuration.kSwerveTranslations[3];

  private final int[] frontLeftChannels = Configuration.kSwerveFLCanID;
  private final int[] frontRightChannels = Configuration.kSwerveFRCanID;
  private final int[] backLeftChannels = Configuration.kSwerveBLCanID;
  private final int[] backRightChannels = Configuration.kSwerveBRCanID;

  private final SwerveModule m_frontLeft = new SwerveModule(
    frontLeftChannels[0], frontLeftChannels[1], frontLeftChannels[2], frontLeftChannels[3]);

  private final SwerveModule m_frontRight = new SwerveModule(
    frontRightChannels[0], frontRightChannels[1], frontRightChannels[2], frontRightChannels[3]);

  private final SwerveModule m_backLeft = new SwerveModule(
    backLeftChannels[0], backLeftChannels[1], backLeftChannels[2], backLeftChannels[3]);

  private final SwerveModule m_backRight = new SwerveModule(
    backRightChannels[0], backRightChannels[1], backRightChannels[2], backRightChannels[3]);
      
  private final SwerveModule m_modules[] = {m_frontLeft, m_frontRight, m_backLeft, m_backRight};
  private final Pigeon2 m_pigeon2 = new Pigeon2(Configuration.kPigeon2CanID);

  // Creating my kinematics object using the module locations
  SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
  m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation);

  boolean m_turbo = false;

  private final SwerveDriveOdometry m_odometry =
    new SwerveDriveOdometry(
        m_kinematics,
        m_pigeon2.getRotation2d(),
        getModulePositions());

  private final SwerveDrivePoseEstimator m_poseEstimator =
    new SwerveDrivePoseEstimator(
      m_kinematics,
      m_pigeon2.getRotation2d(),
      getModulePositions(),
      new Pose2d()
    );

  private Field2d m_field = new Field2d();

  /** Creates a new DriveSubSystem. */
  public DrivetrainSubsystem() {
    m_pigeon2.reset();

    // https://github.com/mjansen4857/pathplanner/tree/main/examples
    // Configure AutoBuilder
    AutoBuilder.configureHolonomic(
      this::getPose,
      this::resetPose,
      this::getChassisSpeeds,
      this::driveRobotSpeeds,
      Configuration.kPathFollowerConfig,

      () -> {
          // Boolean supplier that controls when the path will be mirrored for the red alliance
          // This will flip the path being followed to the red side of the field.
          // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

          var alliance = DriverStation.getAlliance();
          if (alliance.isPresent()) {
              return alliance.get() == DriverStation.Alliance.Red;
          }
          return false;
      },

      this);

          // Set up custom logging to add the current path to a field 2d widget
    PathPlannerLogging.setLogActivePathCallback((poses) -> m_field.getObject("path").setPoses(poses));

    SmartDashboard.putData("Field", m_field);

      //m_poseEstimator.resetPosition(new Rotation2d(45), getModulePositions(), new Pose2d(7.4, 0.42, new Rotation2d(45)));
  }

  public void periodic() {
    updateOdometry();
    m_field.setRobotPose(getPose());

    LoggedNumber.getInstance().logNumber("pig_yaw", m_pigeon2.getYaw().getValue());
    LoggedNumber.getInstance().logNumber("pig_angle", m_pigeon2.getAngle());
    //SmartDashboard.putData("FieldPos", m_field);
    LoggedNumber.getInstance().logNumber("RobotPoseX", getPose().getX());
    LoggedNumber.getInstance().logNumber("RobotPoseY", getPose().getY());
    LoggedNumber.getInstance().logNumber("RobotPoseDeg", getPose().getRotation().getDegrees());
    LoggedNumber.getInstance().logNumber("RobotPoseX2", m_odometry.getPoseMeters().getX());
    LoggedNumber.getInstance().logNumber("RobotPoseY2", m_odometry.getPoseMeters().getY());
    LoggedNumber.getInstance().logNumber("RobotPoseDeg2", m_odometry.getPoseMeters().getRotation().getDegrees());
    LoggedNumber.getInstance().logNumber("FieldX", m_field.getRobotPose().getX());
    LoggedNumber.getInstance().logNumber("FieldY", m_field.getRobotPose().getY());
    LoggedNumber.getInstance().logNumber("FieldRot", m_field.getRobotPose().getRotation().getDegrees());
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
   * @param m_fieldRelative Whether the provided x and y speeds are relative to the m_field.
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

    for (int i = 0; i < 4; i++) {
      m_modules[i].setDesiredState(swerveModuleStates[i]);
      LoggedNumber.getInstance().logNumber("Swerve_" + i + "_drive", swerveModuleStates[i].speedMetersPerSecond);
      LoggedNumber.getInstance().logNumber("Swerve_" + i + "_angle", swerveModuleStates[i].angle.getDegrees());
    }
    LoggedNumber.getInstance().logNumber("Vx", xSpeed);
    LoggedNumber.getInstance().logNumber("Vy", ySpeed);
    LoggedNumber.getInstance().logNumber("Omega", rot);
  }

  public void driveFieldSpeeds(ChassisSpeeds fieldSpeeds) {
    var relativeSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
      fieldSpeeds, getPose().getRotation());
    
    driveRobotSpeeds(relativeSpeeds);
  }

  public void driveRobotSpeeds(ChassisSpeeds robotSpeeds) {
    System.out.println("driveRobotSpeeds vx: " + robotSpeeds.vxMetersPerSecond + " vy: " + robotSpeeds.vyMetersPerSecond + " rot: " + robotSpeeds.omegaRadiansPerSecond);
    robotSpeeds.omegaRadiansPerSecond = -robotSpeeds.omegaRadiansPerSecond;
    var targetSpeeds = ChassisSpeeds.discretize(robotSpeeds, 0.02);
    var swerveModuleStates = m_kinematics.toSwerveModuleStates(targetSpeeds);

    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Configuration.kMaxSpeed);

    for (int i = 0; i < 4; i++) {
      m_modules[i].setDesiredState(swerveModuleStates[i]);
    }
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
    m_odometry.update(m_pigeon2.getRotation2d(), getModulePositions());
    m_poseEstimator.update(m_pigeon2.getRotation2d(), getModulePositions());
  }

  public Pose2d getPose() {
    // TODO: This is broke
    return m_odometry.getPoseMeters();
    //return m_poseEstimator.getEstimatedPosition();
  }

  public void resetPose(Pose2d resetPose) {
    System.out.println("Reset pose to " + resetPose);
    m_odometry.resetPosition(m_pigeon2.getRotation2d(), getModulePositions(), resetPose);
    m_poseEstimator.resetPosition(m_pigeon2.getRotation2d(), getModulePositions(), resetPose);
  }

  public void setGyro(double robotHeading) {
    m_pigeon2.setYaw(robotHeading);
  }

  public Command resetGyro() {
    return new InstantCommand(() -> {m_pigeon2.reset();});
  }

  public void addVisionMeasurement(Pose2d visionMeasurement, double timestamp) {
    m_poseEstimator.addVisionMeasurement(visionMeasurement, timestamp);
  }

  public Command runPath() {
    // // Load the path you want to follow using its name in the GUI
    PathPlannerPath path = PathPlannerPath.fromPathFile("Test1_Straight");

    // // Create a path following command using AutoBuilder. This will also trigger event markers.
    return AutoBuilder.followPath(path);
  }

  private void setBrakeMode(boolean enabled) {
    for (int i = 0; i < 4; i++) {
      m_modules[i].setBrake(enabled);
    }
  }

  public Command setBrakeModeCmd() {
    return new InstantCommand(() -> setBrakeMode(true));
  }

  public Command setCoastModeCmd() {
    return new InstantCommand(() -> setBrakeMode(false));
  }

  public Command enableTurbo() {
      return new InstantCommand(() -> m_turbo = true);
  }

  public Command disableTurbo() {
      return new InstantCommand(() -> m_turbo = false);
  }

  public void setGains(double kp, double ki, double kd, double ks, double kv) {
    m_frontLeft.setGains(kp, ki, kd, ks, kv);
    m_frontRight.setGains(kp, ki, kd, ks, kv);
    m_backLeft.setGains(kp, ki, kd, ks, kv);
    m_backRight.setGains(kp, ki, kd, ks, kv);
  }

  public void homeSwerve() {
    System.out.println("Setting the zero position for the turning motors");
    for (int i = 0; i < 4; i++) {
      m_modules[i].syncSwerveEncoder(Configuration.kZeroPosition[i]);
    }    
  }

  public void printHomePos() {
    double abspos[] = new double[4];
    for (int i = 0; i < 4; i++) { 
      abspos[i] = m_modules[i].getAbsPos();
      //System.out.println("abspos_" + i + ": " + abspos[i]);
    }
    SmartDashboard.putNumberArray("abspos", abspos);
  }

}
