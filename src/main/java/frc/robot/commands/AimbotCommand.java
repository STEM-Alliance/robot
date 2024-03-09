// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;


import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Configuration;
import frc.robot.LimelightHelpers;
import frc.robot.Robot;


import java.util.Optional;

import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.*;



/** An example command that uses an example subsystem. */
public class AimbotCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  DrivetrainSubsystem m_swerve;
  int m_counter = 0;

  PIDController m_xPID = new PIDController(Configuration.kAimP, Configuration.kAimI, Configuration.kAimD);
  PIDController m_DrivePID = new PIDController(Configuration.kAutoDriveP, Configuration.kAutoDriveI, Configuration.kAutoDriveD);
  boolean m_doneAiming = false;
  double m_tag;

  
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
    var p = SmartDashboard.getNumber("P", Configuration.kAimP);
    var i = SmartDashboard.getNumber("I", Configuration.kAimI);
    var d = SmartDashboard.getNumber("D", Configuration.kAimD);
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
    // LimeLight Values
    LimelightHelpers limelightHelpers = new LimelightHelpers();
    
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry tz = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry tid = table.getEntry("tid");

    double x = tx.getDouble(0);
    double z = tz.getDouble(0.0);
    double area = ta.getDouble(0.0);
    double tidnum = tid.getDouble(0);

    // getting alliance and assigning tag
    Optional<Alliance> ally = DriverStation.getAlliance();
    if (ally.isPresent()) {
      if (ally.get() == Alliance.Red) {
        m_tag = 4;
      }
      if (ally.get() == Alliance.Blue) {
        m_tag = 7;
      }
    }

    // Geting tag and driving to the zero point
    if (tidnum == m_tag)
    {
      double xCaclulated = m_xPID.calculate(x, 0);
      
      if(xCaclulated > Configuration.kAimSpeedLimit)
      {
        xCaclulated = Configuration.kAimSpeedLimit;
      }
      else if (xCaclulated < -Configuration.kAimSpeedLimit)
      {
        xCaclulated = -Configuration.kAimSpeedLimit;
      }
      
      //post to smart dashboard periodically
      SmartDashboard.putNumber("LimelightX", x);
      SmartDashboard.putNumber("LimelightZ", z);
      SmartDashboard.putNumber("LimelightArea", area);
      SmartDashboard.putNumber("xCaculated", xCaclulated);
      
      SmartDashboard.putBoolean("done Aiming", m_doneAiming);
      
      SmartDashboard.putData("Pid", m_xPID);

      // Stoping command
      if (Math.abs(x) < Configuration.kAimbotStop)
      {
        m_doneAiming = true;
        m_swerve.drive(0, 0, 0, false, 0.02);
      }
      else
      {
        m_swerve.drive(0, 0, -xCaclulated , false, 0.02);
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
