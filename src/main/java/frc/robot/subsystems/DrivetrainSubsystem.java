// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Configuration;
import frc.robot.LoggedNumber;
import frc.robot.SwerveModule;

import com.kauailabs.navx.frc.AHRS;
import java.math.*;

public class DrivetrainSubsystem extends SubsystemBase {
    private double m_vx = 0.0;
    private double m_vy = 0.0;
    private double m_omega = 0.0;

    // Locations for the swerve drive modules relative to the robot center.
    Translation2d m_frontLeftLocation = new Translation2d(0.3302, 0.3334);
    Translation2d m_frontRightLocation = new Translation2d(0.3302, -0.3334);
    Translation2d m_backLeftLocation = new Translation2d(-0.3302, 0.3334);
    Translation2d m_backRightLocation = new Translation2d(-0.3302, -0.3334);

    private final SwerveModule m_frontLeft = new SwerveModule(0, 3, 4, 0);
    private final SwerveModule m_frontRight = new SwerveModule(1, 5, 6, 1);
    private final SwerveModule m_backLeft = new SwerveModule(2, 2, 1, 2);
    private final SwerveModule m_backRight = new SwerveModule(3, 8, 7, 3);
    private final SwerveModule m_modules[] = {m_frontLeft, m_frontRight, m_backLeft, m_backRight};
    public final AHRS m_ahrs = new AHRS(SPI.Port.kMXP);

    // Creating my kinematics object using the module locations
    SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
    m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation
    );    

  // TODO: Uncomment when we have all 4 motors.
  private final SwerveDriveOdometry m_odometry =
      new SwerveDriveOdometry(
          m_kinematics,
          m_ahrs.getRotation2d(),
          new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_backLeft.getPosition(),
            m_backRight.getPosition()
          });

  /** Creates a new DriveSubSystem. */
  public DrivetrainSubsystem() {
    m_ahrs.reset();
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
  public void drive(
      double xSpeed, double ySpeed, double rot, boolean fieldRelative, double periodSeconds) {
    var swerveModuleStates =
        m_kinematics.toSwerveModuleStates(
            ChassisSpeeds.discretize(
                fieldRelative
                    ? ChassisSpeeds.fromFieldRelativeSpeeds(
                        xSpeed, ySpeed, rot, m_ahrs.getRotation2d())
                    : new ChassisSpeeds(xSpeed, ySpeed, rot),
                periodSeconds));
    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Configuration.kMaxSpeed);
    // TODO: Only drive one swerve at the moment. Uncomment when we have all 4 motors.
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_backLeft.setDesiredState(swerveModuleStates[2]);
    m_backRight.setDesiredState(swerveModuleStates[3]);

    // We can log things to the Smartdashboard and to a log file. LoggedNumber is what is called a Singleton
    SmartDashboard.putNumber("vx", xSpeed);
    SmartDashboard.putNumber("vy", ySpeed);
    SmartDashboard.putNumber("omega", rot);
  }

  /** Updates the field relative position of the robot. */
  public void updateOdometry() {
    // TODO: Uncomment this when we have all 4 motors.
    m_odometry.update(
        m_ahrs.getRotation2d(),
        new SwerveModulePosition[] {
          m_frontLeft.getPosition(),
          m_frontRight.getPosition(),
          m_backLeft.getPosition(),
          m_backRight.getPosition()
        });
  }

  public InstantCommand setBrakeModeCmd() {
    m_frontLeft.Braking = true;
    m_frontRight.Braking = true;
    m_backLeft.Braking = true;
    m_backRight.Braking = true;
    
    return new InstantCommand();
  }

  public InstantCommand setCoastModeCmd() {
     m_frontLeft.Braking = false;
    m_frontRight.Braking = false;
    m_backLeft.Braking = false;
    m_backRight.Braking = false;

    return new InstantCommand();
  }

  public Command ZeroGyro() {
    m_ahrs.zeroYaw();
      return new InstantCommand();
  }

  public Command disableTurbo() {
    // TODO: Disable turbo mode
      return new InstantCommand();
  }

  public void setGains(double kp, double ki, double kd) {
    m_frontLeft.setGains(kp, ki, kd);
  }

  public void homeSwerve() {
    System.out.println("Setting the zero position for the turning motors");
    for (int i = 0; i < 4; i++) {
      m_modules[i].syncSwerveEncoder(Configuration.kZeroPosition[i]);
    }    
  }

  public void printAbsPos() {
    for (int i = 0; i < 4; i++) {
      m_modules[i].printAbsPos();
    }
  }
}
