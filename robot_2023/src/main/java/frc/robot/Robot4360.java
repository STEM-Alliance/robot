// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import edu.wpi.first.cameraserver.CameraServer;

import edu.wpi.first.math.trajectory.*;
import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import java.util.*;

/**
 * Uses the CameraServer class to automatically capture video from a USB webcam and send it to the
 * FRC dashboard without doing any vision processing. This is the easiest way to get camera images
 * to the dashboard. Just add this to the robotInit() method in your program.
 */

public class Robot4360 extends TimedRobot {
  //private DifferentialDrive m_myRobot;
    private XboxController m_controller1;

    SlewRateLimiter m_forwardFilter = new SlewRateLimiter(Configuration.forward_back_slew_rate);
    SlewRateLimiter m_rotationFilter = new SlewRateLimiter(Configuration.right_left_slew_rate);

    NetworkTableEntry m_autoTime;
    NetworkTableEntry m_lencTable;
    NetworkTableEntry m_rencTable;
    NetworkTableEntry m_moveDistance;

    DriveSubsystem m_robotDrive = new DriveSubsystem();

    Command m_autoCommand;

    @Override
    public void robotInit() {
        // We need to invert one side of the drivetrain so that positive voltages
        // result in both sides moving forward. Depending on how your robot's
        // gearbox is constructed, you might have to invert the left side instead.
        // m_right.setInverted(true);

        m_controller1 = new XboxController(0);

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

    @Override
    public void teleopPeriodic() {

        if (m_autoCommand != null) {
            m_autoCommand.cancel();
        }

        // The rotation needs to be inverted. Otherwise the robot will turn in the wrong direction
        double multiFactor = Configuration.fine_controller_derate;
        if (m_controller1.getYButton())
        {
            multiFactor = 1;
        }

        double joy1[] = new double[4];
        joy1[0] = m_controller1.getLeftY();
        joy1[1] = m_controller1.getLeftX();
        joy1[2] = m_forwardFilter.calculate(m_controller1.getLeftY() * multiFactor);
        joy1[3] = m_rotationFilter.calculate(m_controller1.getLeftX() * multiFactor);

        if (m_controller1.getAButton())
        {
            m_robotDrive.zeroHeading();
            m_robotDrive.resetOdometry(new Pose2d());
        }

        SmartDashboard.putNumberArray("Joy1", joy1);
        m_robotDrive.arcadeDrive(joy1[2], joy1[3]);
    }


    /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */

  /*
    Per the spec the Neo motors have 42 pulses/rotation
    The Neo encoder reports in rotations
    The neo rotates ~8.5 times per one wheel rotation
    The wheels are 6 inches

    The velocity is reported in RPMs, it appears the max is around 5800 RPM or so.

    circumference = 2 * pi * r

    1 rotation of the main drive wheel is 2 * pi * 3 inches = 18.85 inches or 0.479 meters
    Or 1 motor rotation is 0.479 meters/9 = 0.053 meters
    At max speed the robot can go 0.053 meters * 5800 RPM / 60 = 5.12 meters/sec

    So if we are given meters/second for the wheels, we need to convert that into
   */
    double m_start = 0;
    @Override
    public void autonomousInit() {
        m_start = System.nanoTime() / 1E9;
        m_robotDrive.zeroHeading();
        m_autoCommand = getAutonomousCommand();
         // schedule the autonomous command (example)
        if (m_autoCommand != null) {
            m_autoCommand.schedule();
      }
    }

    /** This function is called periodically during autonomous. */
    @Override
    public void autonomousPeriodic() {
        CommandScheduler.getInstance().run();
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        var kDriveKinematics = new DifferentialDriveKinematics(Configuration.TrackWidth);

        // Create config for trajectory
        TrajectoryConfig config =
            new TrajectoryConfig(
                    Configuration.kMaxSpeedMetersPerSecond,
                    Configuration.kMaxAccelerationMetersPerSecondSquared)
                // Add kinematics to ensure max speed is actually obeyed
                .setKinematics(kDriveKinematics);

        // An example trajectory to follow.  All units in meters.
        Trajectory exampleTrajectory =
            TrajectoryGenerator.generateTrajectory(
                // Start at the origin facing the +X direction
                new Pose2d(0, 0, new Rotation2d(0)),
                // Pass through these two interior waypoints, making an 's' curve path
                List.of(new Translation2d(10, 1), new Translation2d(20, -1)),
                // End 3 meters straight ahead of where we started, facing forward
                new Pose2d(30, 0, new Rotation2d(90)),
                // Pass config
                config);

        // Super Simple
        Trajectory moveStraight =
        TrajectoryGenerator.generateTrajectory(
            // Start at the origin facing the +X direction
            new Pose2d(0, 0, m_robotDrive.getHeading()),
            List.of(new Translation2d(5, 0)),
            // End 3 meters straight ahead of where we started, facing forward
            new Pose2d(10, 0, m_robotDrive.getHeading()),
            // Pass config
            config);

        // Box turn
        Trajectory boxturn =
        TrajectoryGenerator.generateTrajectory(
            // Start at the origin facing the +X direction
            new Pose2d(0, 0, new Rotation2d(0)),
            List.of(new Translation2d(10, 0),
                    new Translation2d(10, 10),
                    new Translation2d(0, 10),
                    new Translation2d(0, 0)),
            // End 3 meters straight ahead of where we started, facing forward
            new Pose2d(20, 0, new Rotation2d(0)),
            // Pass config
            config);

        // RamseteCommand ramseteCommand =
        //     new RamseteCommand(
        //         moveStraight,
        //         m_robotDrive::getPose,
        //         new RamseteController(0.5, 0.2),
        //         kDriveKinematics,
        //         m_robotDrive::move,
        //         m_robotDrive);

        double kp = 0.2;
        double ki = 0;

        RamseteCommand ramseteCommand =
        new RamseteCommand(
            moveStraight,
            m_robotDrive::getPose,
            new RamseteController(2, 0.7),
            new SimpleMotorFeedforward(0.1, 0.1),
            kDriveKinematics,
            m_robotDrive::getWheelSpeeds,
            new PIDController(kp, ki, 0),
            new PIDController(kp, ki, 0),
            m_robotDrive::move,
            m_robotDrive);

        // Reset odometry to the starting pose of the trajectory.
        m_robotDrive.resetOdometry(moveStraight.getInitialPose());

        // Run path following command, then stop at the end.
        return ramseteCommand.andThen(() -> m_robotDrive.stop());
    }

    // /**
    //  * Use this to pass the autonomous command to the main {@link Robot} class.
    //  *
    //  * @return the command to run in autonomous
    //  */
    // public Command getTurnCommand() {
    //     // Run path following command, then stop at the end.
    //     return ramseteCommand.andThen(() -> m_robotDrive.stop());
    // }
}
