/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ShooterSS;

public class togglespeed extends CommandBase {
  /**
   * Creates a new togglespeed.
   */

private double setspeed;
private ShooterSS m_subsystem;
  public togglespeed(ShooterSS subsyetm ,double setspeed) {
    this.setspeed = setspeed;
   m_subsystem = subsyetm;
    // Use addRequirements() here to declare subsystem dependencies.
 
  }

  // Called when the command is initially scheduled.

  @Override
  public void initialize() {
   
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    ShooterSS.togglespeed = setspeed;
    SmartDashboard.putNumber("number", ShooterSS.togglespeed );
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
