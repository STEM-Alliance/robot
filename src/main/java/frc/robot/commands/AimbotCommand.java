// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Configuration;
import frc.robot.LimelightHelpers;

import java.text.DecimalFormat;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.*;



/** An example command that uses an example subsystem. */
public class AimbotCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final DriveSubsystem m_subsystem;
  int m_counter = 0;

  PIDController m_xPID = new PIDController(Configuration.kAimP, Configuration.kAimI, Configuration.kAimD);

  boolean m_doneAiming = true;

  /**
   * Creates a new AimbotCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public AimbotCommand(DriveSubsystem subsystem) {
    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    m_subsystem.setBrakeMode();
    var p = SmartDashboard.getNumber("P", 0);
    var i = SmartDashboard.getNumber("I", 0);
    var d = SmartDashboard.getNumber("D", 0);
    m_xPID.setP(p);
    m_xPID.setI(i);
    m_xPID.setD(d);
    System.out.println("starting aimbot");
    m_doneAiming = false;
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    LimelightHelpers limelightHelpers = new LimelightHelpers();

    double tx1 = limelightHelpers.getTX(" ");

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
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
    SmartDashboard.putNumber("x", x); 
   
    if (tidnum == 4)
    {
      double xCacluated = m_xPID.calculate(x, 0);
      SmartDashboard.putNumber("xCaculated", xCacluated);
      if(xCacluated > Configuration.kAimSpeedLimit)
      {
        xCacluated = Configuration.kAimSpeedLimit;
      }
      else if (xCacluated < -Configuration.kAimSpeedLimit)
      {
        xCacluated = -Configuration.kAimSpeedLimit;
      }
      
      SmartDashboard.putData("Pid", m_xPID);
      
      System.out.println("x: " + Math.abs(x));

      // m_aPID.setSetpoint(1);
      if (Math.abs(x) < Configuration.kAimbotStop)
      {
        m_doneAiming = true;
        m_subsystem.arcadeDrive(0, 0, 0);
      }
      else
      {
        m_subsystem.arcadeDrive(0, xCacluated, 0);
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
