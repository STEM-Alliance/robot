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

  // Max velocity is 10 degrees/s and it takes 2 second(s) to accelerate to that speed
  // ProfiledPIDController m_xPID = new ProfiledPIDController(
  //   0.00001, Configuration.kAimI, Configuration.kAimD,
  //   new TrapezoidProfile.Constraints(10, 10 / 2));
  PIDController m_xPID = new PIDController(Configuration.kAimP, Configuration.kAimI, Configuration.kAimD);

  boolean tagFound = false;
  boolean m_doneAiming = false;

  double currentHeadingOffset;

  double targetHeading;
  double robotHeading;

  double m_desiredRotation = 0;

  String m_name = Configuration.kLimelightName;

  /**
   * Creates a new AimbotCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public AimbotCommand2(DrivetrainSubsystem subsystem) {
    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    //addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    //m_subsystem.setBrakeMode();
    // For some reason command scheduler loop overrun, only when run for the first time?
    // var p = SmartDashboard.getNumber("P", 0);
    // var i = SmartDashboard.getNumber("I", 0);
    // var d = SmartDashboard.getNumber("D", 0);
    // m_xPID.setP(Configuration.kAimP);
    // m_xPID.setI(Configuration.kAimI);
    // m_xPID.setD(Configuration.kAimD);
    System.out.println("starting aimbot");
    m_doneAiming = false;
    tagFound = false;

    boolean tv = LimelightHelpers.getTV(m_name);

    // Make sure that there is a tag currently in the view
    // I think getLatestResults does not update no empty when there is no tag detected
    if (tv) {
      updateDesiredTagHeading(true);
      // End the aimbot if the desired tag is not found or there is no tag in the image
    }
    else {
      m_doneAiming = true;
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    if (!m_doneAiming) {
      updateDesiredTagHeading(false);

      double currentHeading = m_subsystem.getContinuousHeading();
      double rotationCalculated = m_xPID.calculate(currentHeading, targetHeading);
      rotationCalculated = MathUtil.clamp(rotationCalculated,
        -Configuration.kAimSpeedLimit, Configuration.kAimSpeedLimit);

      SmartDashboard.putNumber("target heading", targetHeading);
      SmartDashboard.putNumber("current heading", currentHeading);
      SmartDashboard.putNumber("rotation pid", rotationCalculated);
      // SmartDashboard.putNumber("pid setpoint", m_xPID.getSetpoint().position);

      m_desiredRotation = rotationCalculated;

      // if (Math.abs(currentHeading - targetHeading) > Configuration.kAimbotStop) {
      //   ChassisSpeeds driveSpeeds = new ChassisSpeeds(
      //     0, 0, rotationCalculated);
      //   m_subsystem.driveRobotSpeeds(driveSpeeds);
      // }
      // else {
      //   m_doneAiming = false;
      //   ChassisSpeeds driveSpeeds = new ChassisSpeeds(
      //     0, 0, 0);
      //   m_subsystem.driveRobotSpeeds(driveSpeeds);
      // }
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // m_subsystem.driveRobotSpeeds(new ChassisSpeeds(
    //   0, 0, 0));
    m_desiredRotation = 0;
    System.out.println("aimbot stopped");
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_doneAiming;
  }

  private void updateDesiredTagHeading(boolean forceUpdate) {
    LimelightResults results = LimelightHelpers.getLatestResults(m_name);

    for (LimelightTarget_Fiducial target: results.targetingResults.targets_Fiducials) {
      if (target.fiducialID == Helpers.getDesiredAprilTag()) {
        double tagTX = target.tx;
        double tagHeadingOffset = tagTX;

        // If the change in the offset is more than 0.5 degrees, then it is a new frame
        if (Math.abs(tagHeadingOffset - currentHeadingOffset) > 5 || forceUpdate) {
          robotHeading = m_subsystem.getContinuousHeading();
          targetHeading = robotHeading + tagHeadingOffset;
          currentHeadingOffset = tagHeadingOffset;
          tagFound = true;

          // if (forceUpdate) {
          //   m_xPID.setGoal(new TrapezoidProfile.State(targetHeading, 0));
          // }
          // else {
          //   m_xPID.setGoal(targetHeading);
          // }
          break;
        }
      }
    }
    if (!tagFound) {
      m_doneAiming = true;
     }
   }

   public double getDesiredRotation() {
    return m_desiredRotation;
   }
}