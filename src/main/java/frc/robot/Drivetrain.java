// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;
import java.math.*;

public class Drivetrain {
    private double m_vx = 0.0;
    private double m_vy = 0.0;
    private double m_omega = 0.0;

    // Locations for the swerve drive modules relative to the robot center.
    Translation2d m_frontLeftLocation = new Translation2d(0.381, 0.381);
    Translation2d m_frontRightLocation = new Translation2d(0.381, -0.381);
    Translation2d m_backLeftLocation = new Translation2d(-0.381, 0.381);
    Translation2d m_backRightLocation = new Translation2d(-0.381, -0.381);

    private final SwerveModule m_frontLeft = new SwerveModule(1, 2, 0);

    private final AHRS m_ahrs = new AHRS(SPI.Port.kMXP);

    // Creating my kinematics object using the module locations
    SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
    m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation
    );    

  // TODO: Uncomment when we have all 4 motors.
  // private final SwerveDriveOdometry m_odometry =
  //     new SwerveDriveOdometry(
  //         m_kinematics,
  //         m_ahrs.getRotation2d(),
  //         new SwerveModulePosition[] {
  //           m_frontLeft.getPosition(),
  //           m_frontRight.getPosition(),
  //           m_backLeft.getPosition(),
  //           m_backRight.getPosition()
  //         });

  /** Creates a new DriveSubSystem. */
  public Drivetrain() {
    m_ahrs.reset();
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
    // m_frontRight.setDesiredState(swerveModuleStates[1]);
    // m_backLeft.setDesiredState(swerveModuleStates[2]);
    // m_backRight.setDesiredState(swerveModuleStates[3]);

    // We can log things to the Smartdashboard and to a log file. LoggedNumber is what is called a Singleton
    LoggedNumber.getInstance().logNumber("vx", xSpeed, true);
    LoggedNumber.getInstance().logNumber("vy", ySpeed, true);
    LoggedNumber.getInstance().logNumber("omega", rot, true);
    //LoggedNumber.getInstance().logNumber("drive", swerveModuleStates[0].speedMetersPerSecond, true);
    //LoggedNumber.getInstance().logNumber("rot", swerveModuleStates[0].angle.getRadians(), true);
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
}
