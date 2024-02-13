// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Configuration;
import frc.robot.LimelightHelpers;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.DoubleArraySubscriber;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.smartdashboard.*;

import java.lang.Math.*;

/** An example command that uses an example subsystem. */
public class StrafeAimCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final DriveSubsystem m_subsystem;
  int m_counter = 0;

  PIDController m_strafePID = new PIDController(Configuration.kTrapAimPs, Configuration.kTrapAimIs, Configuration.kTrapAimDs);
  DoubleSubscriber m_tid;
  DoubleSubscriber m_tx;

  boolean m_doneAiming = true;

  /**
   * Creates a new StrafeAimCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public StrafeAimCommand(DriveSubsystem subsystem) {
    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    m_tid = table.getDoubleTopic("tid").subscribe(0.0);
    m_tx = table.getDoubleTopic("tx").subscribe(0.0);

    m_subsystem.setBrakeMode();
    var p = SmartDashboard.getNumber("P", 0);
    var i = SmartDashboard.getNumber("I", 0);
    var d = SmartDashboard.getNumber("D", 0);
    m_strafePID.setP(p);
    m_strafePID.setI(i);
    m_strafePID.setD(d);
    System.out.println("starting strafe aim");
    m_doneAiming = false;
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    LimelightHelpers limelightHelpers = new LimelightHelpers();

    //post to smart dashboard periodically
   
    if (m_tid.get() == 4)
    {
      var offset = m_tx.get();
      double strafe_out = m_strafePID.calculate(offset, 0);
      SmartDashboard.putNumber("strafe", strafe_out);
      strafe_out = Math.min(Math.max(strafe_out, -Configuration.kAimSpeedLimit), Configuration.kAimSpeedLimit);
      
      System.out.println("Rot: " + Math.abs(offset));

      // m_aPID.setSetpoint(1);
      if (Math.abs(offset) < Configuration.kAimbotStop)
      {
        m_doneAiming = true;
        m_subsystem.arcadeDrive(0, 0, 0);
      }
      else
      {
        m_subsystem.arcadeDrive(0, 0, strafe_out);
      }
    }
      
 SmartDashboard.putNumber("ZeroCounter", m_counter++);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

    System.out.println("strafe bot stopped");
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_doneAiming;
  }
}
