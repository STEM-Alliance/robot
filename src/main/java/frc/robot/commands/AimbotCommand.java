// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;


import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Configuration;
import frc.robot.LimelightHelpers;
import frc.robot.Robot;

import java.text.DecimalFormat;
import frc.robot.subsystems.DrivetrainSubsystem;
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
  private final DrivetrainSubsystem m_swerve;
  int m_counter = 0;

  PIDController m_RotPID = new PIDController(Configuration.kAimP, Configuration.kAimI, Configuration.kAimD);
  PIDController m_DrivePID = new PIDController(Configuration.kAutoDriveP, Configuration.kAutoDriveI, Configuration.kAutoDriveD);
  boolean m_doneAiming = true;

  /**
   * Creates a new AimbotCommand.
   *
   * @param swerve The subsystem used by this command.
   */
  public AimbotCommand(DrivetrainSubsystem swerve) {
    m_swerve = swerve;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(swerve);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
  
    var p = SmartDashboard.getNumber("P", 0);
    var i = SmartDashboard.getNumber("I", 0);
    var d = SmartDashboard.getNumber("D", 0);
    m_RotPID.setP(p);
    m_RotPID.setI(i);
    m_RotPID.setD(d);
    System.out.println("starting aimbot");
    m_doneAiming = false;
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    LimelightHelpers limelightHelpers = new LimelightHelpers();

    double tx1 = LimelightHelpers.getTX(" ");

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry tz = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry tid = table.getEntry("tid");

    //read values periodically
    double x = tx.getDouble(0.0);
    double z = tz.getDouble(0.0);
    double area = ta.getDouble(0.0);
    double tidnum = tid.getDouble(0);

    //post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightZ", z);
    SmartDashboard.putNumber("LimelightArea", area);
    SmartDashboard.putNumber("x", x); 
   
    if (tidnum == 4)
    {
      double RotCaclulated = m_RotPID.calculate(x, 0);
      SmartDashboard.putNumber("xCaculated", RotCaclulated);
      if(RotCaclulated > Configuration.kAimSpeedLimit)
      {
        RotCaclulated = Configuration.kAimSpeedLimit;
      }
      else if (RotCaclulated < -Configuration.kAimSpeedLimit)
      {
        RotCaclulated = -Configuration.kAimSpeedLimit;
      }
      
      SmartDashboard.putData("Pid", m_RotPID);
      
      System.out.println("x: " + Math.abs(x));

      // m_aPID.setSetpoint(1);
      if (Math.abs(x) < Configuration.kAimbotStop)
      {
        m_doneAiming = true;
        m_swerve.drive(0, 0, 0, false, 0);
      }
      else
      {
        m_swerve.drive(0, 0, RotCaclulated, false, 0);
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
