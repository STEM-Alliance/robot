// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.ClimbingSystem;
import edu.wpi.first.units.Time;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

/** An example command that uses an example subsystem. */
public class ClimbingCommand extends Command {
  private final ClimbingSystem subSystem;
  
  /**
   * Creates a new ClimbingSystem.
   *
   * @param subsystem The subsystem used by this command.
   */
  public ClimbingCommand(ClimbingSystem m_subSystem) {

    subSystem = m_subSystem;
    addRequirements(m_subSystem);
    
    // Use addRequirements() here to declare subsystem dependencies.
    
  }
  // Called when the command is initially scheduled.
  @Override
  public void initialize()
  {
    subSystem.leftCLimber.set(-.5);
    subSystem.rightCLimber.set(-.5);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    subSystem.leftCLimber.set(0);
    subSystem.rightCLimber.set(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
