// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

import java.text.DecimalFormat;

import edu.wpi.first.apriltag.AprilTagDetector.Config;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.*;
import frc.robot.*;
import frc.robot.LimelightHelpers.LimelightResults;
import frc.robot.LimelightHelpers.LimelightTarget_Fiducial;


/** An example command that uses an example subsystem. */
public class AimbotCommand2 extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final DrivetrainSubsystem m_subsystem;
  int m_counter = 0;

  PIDController m_xPID = new PIDController(Configuration.kAimP, Configuration.kAimI, Configuration.kAimD);

  boolean tagFound = false;
  boolean m_doneAiming = true;

  double targetHeading;
  double robotHeading;

  /**
   * Creates a new AimbotCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public AimbotCommand2(DrivetrainSubsystem subsystem) {
    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    //m_subsystem.setBrakeMode();
    // For some reason command scheduler loop overrun, only when run for the first time?
    var p = SmartDashboard.getNumber("P", 0);
    var i = SmartDashboard.getNumber("I", 0);
    var d = SmartDashboard.getNumber("D", 0);
    m_xPID.setP(Configuration.kAimP);
    m_xPID.setI(Configuration.kAimI);
    m_xPID.setD(Configuration.kAimD);
    System.out.println("starting aimbot");
    m_doneAiming = false;

    LimelightResults results = LimelightHelpers.getLatestResults(
      Configuration.kLimelightName);

    for (LimelightTarget_Fiducial target: results.targetingResults.targets_Fiducials) {
      if (target.fiducialID == Helpers.getDesiredAprilTag()) {
        double tagTX = target.tx;
        double tagHeadingOffset = tagTX;

        robotHeading = m_subsystem.getContinuousHeading();
        targetHeading = robotHeading + tagHeadingOffset;
        tagFound = true;
        break;
      }
    }
    if (!tagFound) {
      m_doneAiming = true;
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    if (!m_doneAiming) {
      double currentHeading = m_subsystem.getContinuousHeading();
      double rotationCalculated = m_xPID.calculate(currentHeading, targetHeading);
      rotationCalculated = MathUtil.clamp(rotationCalculated,
        -Configuration.kAimSpeedLimit, Configuration.kAimSpeedLimit);

      ChassisSpeeds driveSpeeds = new ChassisSpeeds(
        0, 0, rotationCalculated);

      SmartDashboard.putNumber("target heading", targetHeading);
      SmartDashboard.putNumber("current heading", currentHeading);
      SmartDashboard.putNumber("rotation pid", rotationCalculated);

      /* Find a better way to do this, make it slow down before reaching the target
      instead of overshooting the target */
      if (Math.abs(currentHeading - targetHeading) > Configuration.kAimbotStop) {
        m_subsystem.driveRobotSpeeds(driveSpeeds);
      }
      else {
        m_doneAiming = true;
      }
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_subsystem.driveRobotSpeeds(new ChassisSpeeds(
      0, 0, 0));

    System.out.println("aimbot stopped");
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_doneAiming;
  }
}
