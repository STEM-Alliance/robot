// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Configuration;
import frc.robot.subsystems.ClimbingSystem;

import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.units.Time;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

/** An example command that uses an example subsystem. */
public class ClimbingCommand extends Command {
  private final ClimbingSystem subSystem;
  private PIDController m_pidController;
  private double leftSetpoint;
  private double rightSetpoint;
  private RelativeEncoder leftEncoder;
  private RelativeEncoder rightEncoder;
  /**
   * Creates a new ClimbingSystem.
   *
   * @param subsystem The subsystem used by this command.
   */
  public ClimbingCommand(ClimbingSystem m_subSystem) {

    subSystem = m_subSystem;
    addRequirements(m_subSystem);
    
    m_pidController = new PIDController(Configuration.climbP, Configuration.climbI, Configuration.climbD);
    SmartDashboard.putData("climbPID", m_pidController);
    // Use addRequirements() here to declare subsystem dependencies.
    
    leftEncoder = m_subSystem.leftCLimber.getEncoder();
    rightEncoder = m_subSystem.rightCLimber.getEncoder();



  }
  // Called when the command is initially scheduled.
  @Override
  public void initialize()
  {
    // m_pidController.setSetpoint(0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    subSystem.leftCLimber.set(m_pidController.calculate(leftEncoder.getPosition(),leftSetpoint));
    subSystem.rightCLimber.set(m_pidController.calculate(rightEncoder.getPosition(),rightSetpoint));
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {

  
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
