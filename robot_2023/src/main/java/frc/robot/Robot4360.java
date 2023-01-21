// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.lang.Math;

import edu.wpi.first.cameraserver.CameraServer;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import edu.wpi.first.math.trajectory.*;
import edu.wpi.first.math.util.Units;

import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.math.controller.*;
import edu.wpi.first.math.controller.RamseteController;

import java.util.function.*;
import java.util.*;

/**
 * Uses the CameraServer class to automatically capture video from a USB webcam and send it to the
 * FRC dashboard without doing any vision processing. This is the easiest way to get camera images
 * to the dashboard. Just add this to the robotInit() method in your program.
 */

 

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
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

        SmartDashboard.putNumberArray("Joy1", joy1);
        m_robotDrive.arcadeDrive(joy1[2], joy1[3]);
        // m_myRobot.arcadeDrive(m_forwardFilter.calculate(m_controller1.getLeftY() * multiFactor), 
        //                       m_rotationFilter.calculate(m_controller1.getLeftX()) * multiFactor);
        // SmartDashboard.putNumber("left_enc", m_lenc.getPosition());
        // SmartDashboard.putNumber("right_enc", m_renc.getPosition());
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
  18 pulses per rotation
  each wheel rotation is about 16 inches
  Therefore we have 16/18 = 0.88888 inches per pulse
  2.25 e-2 meters/pulse
   */ 
    double m_start = 0;
    double m_pulses_to_meters = 0.02257777777777777777777777777778;
    @Override
    public void autonomousInit() {
        m_start = System.nanoTime() / 1E9;
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
                List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
                // End 3 meters straight ahead of where we started, facing forward
                new Pose2d(3, 0, new Rotation2d(0)),
                // Pass config
                config);

        RamseteCommand ramseteCommand =
            new RamseteCommand(
                exampleTrajectory,
                m_robotDrive::getPose,
                new RamseteController(2, 0.5),
                kDriveKinematics,
                m_robotDrive::move,
                m_robotDrive);

        // Reset odometry to the starting pose of the trajectory.
        m_robotDrive.resetOdometry(exampleTrajectory.getInitialPose());

        // Run path following command, then stop at the end.
        return ramseteCommand.andThen(() -> m_robotDrive.stop());
    }
}
