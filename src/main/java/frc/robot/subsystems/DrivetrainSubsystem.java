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
    Translation2d m_frontLeftLocation = new Translation2d(0.3048, 0.3048);
    Translation2d m_frontRightLocation = new Translation2d(0.3048, -0.3048);
    Translation2d m_backLeftLocation = new Translation2d(-0.3048, 0.3048);
    Translation2d m_backRightLocation = new Translation2d(-0.3048, -0.3048);

    // private final SwerveModule m_frontLeft = new SwerveModule(0, 1, 2, 0);
    // private final SwerveModule m_frontRight = new SwerveModule(1, 3, 4, 1);
    // private final SwerveModule m_backLeft = new SwerveModule(2, 5, 6, 2);
    // private final SwerveModule m_backRight = new SwerveModule(3, 7, 8, 3);

    private final SwerveModule m_frontLeft = new SwerveModule(0, 5, 6, 0);
    private final SwerveModule m_frontRight = new SwerveModule(1, 1, 2, 1);
    private final SwerveModule m_backLeft = new SwerveModule(2, 7, 8, 2);
    private final SwerveModule m_backRight = new SwerveModule(3, 3, 4, 3);

    private final SwerveModule m_modules[] = {m_frontLeft, m_frontRight, m_backLeft, m_backRight};

    private final AHRS m_ahrs = new AHRS(SPI.Port.kMXP);

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
    for (int i = 0; i < 4; i++)
    {
      m_modules[i].setDesiredState(swerveModuleStates[i]);
    }

    // We can log things to the Smartdashboard and to a log file. LoggedNumber is what is called a Singleton
    SmartDashboard.putNumber("vx", xSpeed);
    SmartDashboard.putNumber("vy", ySpeed);
    SmartDashboard.putNumber("omega", rot);
  }

  /** Updates the field relative position of the robot. */
  public void updateOdometry() {
    // TODO: Uncomment this when we have all 4 motors.
    // m_odometry.update(
    //     m_gyro.getRotation2d(),
    //     new SwerveModulePosition[] {
    //       m_frontLeft.getPosition(),
    //       m_frontRight.getPosition(),
    //       m_backLeft.getPosition(),
    //       m_backRight.getPosition()
    //     });
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
  }

  public void homeSwerve() {
    System.out.println("Setting the zero position for the turning motors");
    for (int i = 0; i < 4; i++) {
      m_modules[i].syncSwerveEncoder(Configuration.kZeroPosition[i]);
    }    
  }

  public void printPos() {
    for (int i = 0; i < 4; i++)
    {
      m_modules[i].printPos();
    }
  } 
}
