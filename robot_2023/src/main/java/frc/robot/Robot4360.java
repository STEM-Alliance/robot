// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.SubSystems.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import edu.wpi.first.math.trajectory.*;
import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import java.util.*;
import java.io.File;

/**
 * Uses the CameraServer class to automatically capture video from a USB webcam and send it to the
 * FRC dashboard without doing any vision processing. This is the easiest way to get camera images
 * to the dashboard. Just add this to the robotInit() method in your program.
 */

public class Robot4360 extends TimedRobot {
    private XboxController m_controller1;

    DriveSubsystem m_robotDrive = new DriveSubsystem(2  , 3, 1, 4);
    OneMotorGripper m_gripper = new OneMotorGripper(10);
    ElevatorSubsystem m_ElevatorSubsystem = new ElevatorSubsystem(7,6);
    LEDSubsystem m_leds = new LEDSubsystem(0, 1, 2);

    Command m_autoCommand;
    Command m_driveCommand;
    Command m_ElveatorCommand;
    

    
    @Override
    public void robotInit() {
        // We need to invert one side of the drivetrain so that positive voltages
        // result in both sides moving forward. Depending on how your robot's
        // gearbox is constructed, you might have to invert the left side instead.
        // m_right.setInverted(true);

        m_controller1 = new XboxController(0);
        XboxController m_controller2 = new XboxController(1);
        final JoystickButton buttonA = new JoystickButton(m_controller2, XboxController.Button.kA.value);
        final JoystickButton buttonB = new JoystickButton(m_controller2, XboxController.Button.kB.value);
        final JoystickButton buttonX = new JoystickButton(m_controller2, XboxController.Button.kX.value);
        
        buttonA.onTrue(m_gripper.open());
        buttonB.onTrue(m_gripper.close());
        buttonX.onTrue(m_gripper.Stop());
        buttonX.onFalse(m_gripper.Go());
               
        m_driveCommand = new RunCommand(() -> m_robotDrive.arcadeDrive(-m_controller1.getLeftY(), -m_controller1.getLeftX()), m_robotDrive);
        m_ElveatorCommand = new RunCommand(() -> m_ElevatorSubsystem.control( m_controller2.getLeftY(), m_controller2.getRightY()) , m_ElevatorSubsystem);
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
        m_driveCommand.schedule();
        m_ElveatorCommand.schedule();

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
        var kDriveKinematics = new DifferentialDriveKinematics(Configuration.TrackWidthInMeters);

        // Reset odometry to the starting pose of the trajectory.
        m_robotDrive.resetOdometry(new Pose2d(0, 0, m_robotDrive.getHeading()));

        // Create config for trajectory
        TrajectoryConfig config =
            new TrajectoryConfig(
                    Configuration.kMaxSpeedMetersPerSecond,
                    Configuration.kMaxAccelerationMetersPerSecondSquared)
                // Add kinematics to ensure max speed is actually obeyed
                .setKinematics(kDriveKinematics);

        // An example trajectory to follow.  All units in meters.
        Trajectory curve =
            TrajectoryGenerator.generateTrajectory(
                // Start at the origin facing the +X direction
                new Pose2d(0, 0, m_robotDrive.getHeading()),
                // Pass through these two interior waypoints, making an 's' curve path
                List.of(new Translation2d(10, 3), new Translation2d(20, -3)),
                // End 3 meters straight ahead of where we started, facing forward
                new Pose2d(30, 3, Rotation2d.fromDegrees(160)),
                // Pass config
                config);

        // Super Simple
        Trajectory moveStraight =
        TrajectoryGenerator.generateTrajectory(
            // Start at the origin facing the +X direction
            new Pose2d(0, 0, m_robotDrive.getHeading()),
            List.of(new Translation2d(3, 0)),
            // End 3 meters straight ahead of where we started, facing forward
            new Pose2d(6, 0, Rotation2d.fromDegrees(0)),
            // Pass config
            config);

        // Box turn
        Trajectory complex =
        TrajectoryGenerator.generateTrajectory(
            // Start at the origin facing the +X direction
            new Pose2d(0, 0, new Rotation2d(0)),
            List.of(new Translation2d(4, 0),
                    new Translation2d(4, 4)),
            // End 3 meters straight ahead of where we started, facing forward
            new Pose2d(5, 5, new Rotation2d(-90)),
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

        //double kp = 2.2193E-07;
        double kp = 2.2193E-03;
        double ki = 0;
        double ks = 0.068221;
        double kv = 2.3938;

        RamseteCommand ramseteCommand =
        new RamseteCommand(
            curve,
            m_robotDrive::getPose,
            new RamseteController(2, 0.7),
            new SimpleMotorFeedforward(ks, kv),
            kDriveKinematics,
            m_robotDrive::getWheelSpeeds,
            new PIDController(kp, ki, 0),
            new PIDController(kp, ki, 0),
            m_robotDrive::move,
            m_robotDrive);

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
