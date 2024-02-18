// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

import java.text.DecimalFormat;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.*;

import frc.robot.*;

/** An example command that uses an example subsystem. */
public class StopBotCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final DrivetrainSubsystem m_subsystem;
  int m_counter = 0;

  PIDController m_xPID = new PIDController(Configuration.kAimP, Configuration.kAimI, Configuration.kAimD);

  boolean m_doneAiming = true;

  /**
   * Creates a new StopBotCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public StopBotCommand(DrivetrainSubsystem subsystem) {
    m_subsystem = subsystem;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    //m_subsystem.setBrakeMode();
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
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry botpose = table.getEntry("botpose	");
    NetworkTableEntry tid = table.getEntry("tid");

    //read values periodically
    var tidnum = tid.getInteger(0);
    var botposeData = botpose.getDoubleArray(new double[] {});
    double x = botposeData[0];
    double y = botposeData[1];

    if (tidnum == Helpers.getDesiredAprilTag())
    {
      // Get the distance to the tag
      double r = Math.sqrt(x*x + y*y);
      if (r < 5)
      {
        //m_subsystem.stopRobot();
        m_doneAiming = true;
      }
    }
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
