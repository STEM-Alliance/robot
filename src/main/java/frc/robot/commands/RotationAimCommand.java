// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Configuration;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.DoubleArraySubscriber;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.smartdashboard.*;

import java.lang.Math.*;

/** An example command that uses an example subsystem. */
public class RotationAimCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final DrivetrainSubsystem m_subsystem;
  int m_counter = 0;

  PIDController m_rotationPID = new PIDController(Configuration.kTrapAimPr, Configuration.kTrapAimIr, Configuration.kTrapAimDr);
  DoubleArraySubscriber m_botpose;
  DoubleSubscriber m_tid;

  boolean m_doneAiming = true;

  /**
   * Creates a new RotationAimCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public RotationAimCommand(DrivetrainSubsystem subsystem) {
    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    m_botpose = table.getDoubleArrayTopic("botpose").subscribe(new double [] {});
    m_tid = table.getDoubleTopic("tid").subscribe(0.0);

    //m_subsystem.setBrakeMode();
    var p = SmartDashboard.getNumber("P", 0);
    var i = SmartDashboard.getNumber("I", 0);
    var d = SmartDashboard.getNumber("D", 0);
    m_rotationPID.setP(p);
    m_rotationPID.setI(i);
    m_rotationPID.setD(d);
    System.out.println("starting strafe aim");
    m_doneAiming = false;
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    //post to smart dashboard periodically
    if (m_tid.get() == 4)
    {
      var rotation = m_botpose.get()[3];
      double rot_out = m_rotationPID.calculate(rotation, 0);
      SmartDashboard.putNumber("strafe", rot_out);
      rot_out = Math.min(Math.max(rot_out, -Configuration.kAimSpeedLimit), Configuration.kAimSpeedLimit);
      
      System.out.println("Rot: " + Math.abs(rotation));

      // m_aPID.setSetpoint(1);
      if (Math.abs(rotation) < Configuration.kAimbotStop)
      {
        m_doneAiming = true;
        // TODO: Replace with swerve
        //m_subsystem.arcadeDrive(0, 0, 0);
      }
      else
      {
        // TODO: Replace with swerve
        //m_subsystem.arcadeDrive(0, rot_out, 0);
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
