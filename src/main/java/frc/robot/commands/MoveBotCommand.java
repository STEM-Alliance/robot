// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

import java.text.DecimalFormat;

import edu.wpi.first.apriltag.AprilTagDetector.Config;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.*;
import frc.robot.*;


/** An example command that uses an example subsystem. */
public class MoveBotCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final DrivetrainSubsystem m_subsystem;
  int m_counter = 0;
  Pose2d m_initialPose;
  double m_iniitalTime;

  /**
   * Creates a new MoveBotCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public MoveBotCommand(DrivetrainSubsystem subsystem) {
    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    m_initialPose = m_subsystem.getPose();
    m_iniitalTime = Timer.getFPGATimestamp();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    m_subsystem.controllerDrive(-0.3, 0, 0, false, 0.02);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

    System.out.println("MoveBot stopped");
    m_subsystem.controllerDrive(0, 0, 0, false, 0.02);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    Pose2d curPose = m_subsystem.getPose();
    double xdiff = curPose.getX() - m_initialPose.getX();
    double ydiff = curPose.getY() - m_initialPose.getY();
    System.out.println("xdiff: " + xdiff + " ydiff: " + ydiff);
    double diffTime = Timer.getFPGATimestamp() - m_iniitalTime;
    if (diffTime > 2.75) {
      return true;
    }
    return false;
  }
}
