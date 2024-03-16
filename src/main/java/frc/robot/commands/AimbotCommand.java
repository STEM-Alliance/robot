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


/** An example command that uses an example subsystem. */
public class AimbotCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final DrivetrainSubsystem m_subsystem;
  int m_counter = 0;

  PIDController m_xPID = new PIDController(Configuration.kAimP, Configuration.kAimI, Configuration.kAimD);

  boolean m_doneAiming = true;

  /**
   * Creates a new AimbotCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public AimbotCommand(DrivetrainSubsystem subsystem) {
    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);

    // SmartDashboard.getNumber("P", Configuration.kAimP);
    // SmartDashboard.getNumber("I", Configuration.kAimI);
    // SmartDashboard.getNumber("D", Configuration.kAimD);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    //m_subsystem.setBrakeMode();
    m_xPID.setP(Configuration.kAimP);
    m_xPID.setI(Configuration.kAimI);
    m_xPID.setD(Configuration.kAimD)  ;
    // var p = SmartDashboard.getNumber("P", Configuration.kAimP);
    // var i = SmartDashboard.getNumber("I", Configuration.kAimI);
    // var d = SmartDashboard.getNumber("D", Configuration.kAimD);
    // m_xPID.setP(p);
    // m_xPID.setI(i);
    // m_xPID.setD(d)  ;
    System.out.println("starting aimbot");
    m_doneAiming = false;
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    NetworkTable table = NetworkTableInstance.getDefault( ).getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry tid = table.getEntry("tid");

    //read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);
    double tidnum = tid.getDouble(0);

    //post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
   
    if (tidnum == Helpers.getDesiredAprilTag())
    {
      double xCacluated = m_xPID.calculate(-x, 0);
      SmartDashboard.putNumber("xCaculated", xCacluated);
      if(xCacluated > Configuration.kAimSpeedLimit)
      {
        xCacluated = Configuration.kAimSpeedLimit;
      }
      else if (xCacluated < -Configuration.kAimSpeedLimit)
      {
        xCacluated = -Configuration.kAimSpeedLimit;
      }
      
      SmartDashboard.putData("AimPid", m_xPID);
      
      System.out.println("x: " + Math.abs(x));

      if (Math.abs(x) < Configuration.kAimbotStop)
      {
        m_doneAiming = true;
        //TODO: Replace with swerve
        ChassisSpeeds robotSpeed = new ChassisSpeeds(0, 0, 0);
        m_subsystem.driveRobotSpeeds(robotSpeed);
    }
      else
      {
        //TODO: Replace with 
        ChassisSpeeds robotSpeed = new ChassisSpeeds(0, 0, xCacluated);
        m_subsystem.driveRobotSpeeds(robotSpeed);
      }
    }
      
 SmartDashboard.putNumber("ZeroCounter", m_counter++);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

    System.out.println("aimbot stopped");
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_doneAiming;
  }
}
