// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.SubSystems.*;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.TimedRobot;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.PWM;

/**
 * Uses the CameraServer class to automatically capture video from a USB webcam and send it to the
 * FRC dashboard without doing any vision processing. This is the easiest way to get camera images
 * to the dashboard. Just add this to the robotInit() method in your program.
 */

public class Robot7048 extends TimedRobot {
    private CommandXboxController m_controller1 = new CommandXboxController(0);
    private CommandXboxController m_controller2 = new CommandXboxController(1);

    private PWM m_leds = new PWM(0);

    DriveSubsystem m_robotDrive = new DriveSubsystem(1, 2, 3, 4, 5);
    //GripperSubsystem m_gripper = new GripperSubsystem(20, 21, 16);
    ElevatorSubsystem m_ElevatorSubsystem = new ElevatorSubsystem(6);
    PneumaticSubsystem m_pneumatics = new PneumaticSubsystem(0);

    Command m_autoCommand;
    Command m_driveCommand;
    Command m_elevatorCommand;

    // Auto select
    final String kLevel = "AutoLevel";
    final String kPlaceCone = "PlaceCone";
    String m_autoSelected;
    final SendableChooser<String> m_chooser = new SendableChooser<>();

    @Override
    public void robotInit() {
        final Trigger brake = m_controller1.b();
        final Trigger coast = m_controller1.a();
        final Trigger enTurbo = m_controller1.rightTrigger();
        final Trigger disTurbo = m_controller1.x();
        final Trigger toggleHDrive = m_controller1.rightBumper();
        toggleHDrive.onTrue(m_pneumatics.toggleHDrive());

        final Trigger gripper_control = m_controller2.leftTrigger();
        final Trigger extend_control = m_controller2.rightTrigger();
        final Trigger high = m_controller2.y();
        final Trigger medium = m_controller2.b();
        final Trigger low = m_controller2.a();
        final Trigger leftBumper = m_controller2.leftBumper();
        final Trigger rightBumper = m_controller2.rightBumper();
        final Trigger leftTrigger = m_controller2.axisGreaterThan(XboxController.Axis.kLeftTrigger.value, 0.5);
        final Trigger rightTrigger = m_controller2.axisGreaterThan(XboxController.Axis.kRightTrigger.value, 0.5);
        final Trigger up = m_controller2.pov(0);
        final Trigger down = m_controller2.pov(180);
        final Trigger left = m_controller2.pov(270);
        final Trigger right = m_controller2.pov(90);
        final Trigger retractHome = m_controller2.x();

        brake.onTrue(m_robotDrive.setBrakeModeCmd());
        coast.onTrue(m_robotDrive.setCoastModeCmd());
        enTurbo.onTrue(m_robotDrive.enableTurbo());
        enTurbo.onFalse(m_robotDrive.disableTurbo());

        //gripper_control.onTrue(m_pneumatics.openClaw());
        //gripper_control.onFalse(m_pneumatics.closeClaw());
        gripper_control.toggleOnTrue(m_pneumatics.toggleGripper());
        extend_control.onTrue(m_pneumatics.extendArm());
        extend_control.onFalse(m_pneumatics.retractArm());

        m_driveCommand = new RunCommand(() -> m_robotDrive.arcadeDrive(-m_controller1.getLeftY(), -m_controller1.getLeftX(), m_controller1.getRightX(), m_pneumatics), m_robotDrive);
        m_elevatorCommand = new RunCommand(() -> m_ElevatorSubsystem.control(m_controller2.getRightY(), m_controller2.getLeftY()), m_ElevatorSubsystem);

        high.onTrue(m_ElevatorSubsystem.MoveArmToHigh());
        medium.onTrue(m_ElevatorSubsystem.MoveArmToMid());
        low.onTrue(m_ElevatorSubsystem.MoveArmToLow());

        // TODO: These don't work right now.
        //buttonA.onTrue(m_ElevatorSubsystem.MoveArmToLow());
        // buttonB.onTrue(m_ElevatorSubsystem.MoveArmToMid());
        // buttonY.onTrue(m_ElevatorSubsystem.MoveArmToHigh());
        //left.whileTrue(m_gripper.slideLeft());
        //right.whileTrue(m_gripper.slideRight());
        //up.whileTrue(m_gripper.openGripper());
        //down.whileTrue(m_gripper.closeGripper());
        //retractHome.onTrue(m_ElevatorSubsystem.RetractHome());

        //leftBumper.whileTrue(m_gripper.rotateLeft());
        //rightBumper.whileTrue(m_gripper.rotateRight());

        CameraServer.startAutomaticCapture();

        //Get the default instance of NetworkTables that was created automatically
        //when your program starts
        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        // Attempting to get the driver station position
        NetworkTable fmsInfo = inst.getTable("FMSInfo");
        NetworkTableEntry m_driverStationPos = fmsInfo.getEntry("StationNumber");
        Number pos = m_driverStationPos.getNumber(0);

        System.out.println("Driver Station number: " + pos.toString());
        System.out.println("Robot starting");

        m_chooser.setDefaultOption(kLevel, kLevel);
        m_chooser.addOption(kPlaceCone, kPlaceCone);
        SmartDashboard.putData("Auto choices", m_chooser);
    }

    @Override
    public void teleopPeriodic() {

        if (m_autoCommand != null) {
            m_autoCommand.cancel();
        }
        m_driveCommand.schedule();
        m_elevatorCommand.schedule();
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

        var time = System.currentTimeMillis();
        var led_sp = ((time % 60) / 30) - 1;
        m_leds.setSpeed(led_sp);
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
        m_autoSelected = m_chooser.getSelected();
        //m_autoSelected = kPlaceCone;
        System.out.println("Auto selected: " + m_autoSelected);
        switch (m_autoSelected)
        {
            case kLevel:
                // Put custom auto code here
                m_autoCommand = getAutoLevel();
                System.out.println("Auto level mode");
            break;
            case kPlaceCone:
            default:
                // Put default auto code here
                m_autoCommand = getPlaceConeAuto();
                System.out.println("Place Cone mode");
                break;
        }

        //m_autoCommand = getAutonomousCommand();
        //m_autoCommand = m_robotDrive.autoLevel();
         // schedule the autonomous command (example)
        if (m_autoCommand != null) {
            m_autoCommand.schedule();
      }
    }

    /** This function is called periodically  during autonomous. */
    @Override
    public void autonomousPeriodic() {
        CommandScheduler.getInstance().run();
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getPlaceConeAuto()
    {
        // Lift arm, drive forward, extend arm, open gripper, retract arm, drive back
        return m_ElevatorSubsystem.MoveArmToPosition(38).andThen(
                m_pneumatics.extendArm().andThen(
                new WaitCommand(2 ).andThen(
                m_pneumatics.openClaw().andThen(
                new WaitCommand(2).andThen(
                m_pneumatics.retractArm().andThen(
                m_pneumatics.closeClaw().andThen(
                new WaitCommand(2).andThen(
                m_robotDrive.driveDistance(-5).andThen(
                m_ElevatorSubsystem.MoveArmToPosition(5))))))))));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutoLevel()
    {
        // Lift arm, drive forward, extend arm, open gripper, retract arm, drive back
        return m_robotDrive.driveDistance(3.1).andThen(
                m_robotDrive.autoLevel());
    }
}
