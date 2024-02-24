// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;


import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Configuration;
import frc.robot.LimelightHelpers;
import frc.robot.Robot;

import java.text.DecimalFormat;
import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.*;



/** An example command that uses an example subsystem. */
public class DriveCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  DrivetrainSubsystem m_swerve;
  boolean m_fieldRelative;
  CommandXboxController m_controller1;
  // Slew rate limiters to make joystick inputs more gentle; 1/3 sec from 0 to 1.
  private final SlewRateLimiter m_xspeedLimiter = new SlewRateLimiter(Configuration.kVxSlewRateLimit);
  private final SlewRateLimiter m_yspeedLimiter = new SlewRateLimiter(Configuration.kVySlewRateLimit);
  private final SlewRateLimiter m_rotLimiter = new SlewRateLimiter(Configuration.kOmegaSlewRateLimit);

  /**
   * Creates a new DriveCommand.
   *
   * @param swerve The subsystem used by this command.
   */
  public DriveCommand(DrivetrainSubsystem swerve, boolean fieldRelative, CommandXboxController controller) {
    m_swerve = swerve;
    m_fieldRelative = fieldRelative;
    m_controller1 = controller;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(swerve);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    // Get the x speed. We are inverting this because Xbox controllers return
    // negative values when we push forward.
    final var xSpeed =
        -m_xspeedLimiter.calculate(MathUtil.applyDeadband(m_controller1.getLeftY(), Configuration.GeneralDeadband))
            * Configuration.kMaxSpeed;

    // Get the y speed or sideways/strafe speed. We are inverting this because
    // we want a positive value when we pull to the left. Xbox controllers
    // return positive values when you pull to the right by default.
    final var ySpeed =
        -m_yspeedLimiter.calculate(MathUtil.applyDeadband(m_controller1.getLeftX(), Configuration.GeneralDeadband))
            * Configuration.kMaxSpeed;

    // Get the rate of angular rotation. We are inverting this because we want a
    // positive value when we pull to the left (remember, CCW is positive in
    // mathematics). Xbox controllers return positive values when you pull to
    // the right by default.
    var rot =
        -m_rotLimiter.calculate(MathUtil.applyDeadband(m_controller1.getRightX(), Configuration.GeneralDeadband))
            * Configuration.kMaxAngularSpeed;

    rot = rot * (1 - (0.9 * (Math.abs(xSpeed) + Math.abs(ySpeed))));
    // TODO: Replace this magic number
    m_swerve.drive(xSpeed, ySpeed, rot, m_fieldRelative, 0.02);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

    System.out.println("aimbot stopped");
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
