// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.IntakeSubSystem;
import edu.wpi.first.units.Time;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

/** An example command that uses an example subsystem. */
public class IntakeCommand extends Command {
  private final IntakeSubSystem subSystem;
  /**
   * Creates a new IntakeCommand.
   *
   * @param subsystem The subsystem used by this command.
   * 
   */
  boolean reverse;
  public IntakeCommand(IntakeSubSystem m_subSystem, boolean reverse) {
    subSystem = m_subSystem;
    addRequirements(m_subSystem);

    // Use addRequirements() here to declare subsystem dependencies.
   
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize()
  {
    
    subSystem.m_intake.set(1);
    subSystem.m_midintake.set(-1);
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  
    if (reverse){
      subSystem.m_midintake.set(1);
    }
    else 
    {
      subSystem.m_midintake.set(-1);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    subSystem.m_intake.set(0);
    subSystem.m_midintake.set(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
