/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.reuse.hardware.lowleveldriver.BlinkinPatterns;
import frc.robot.subsystems.ColorSensor;
import frc.robot.subsystems.LedSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * An example command that uses an example subsystem.
 */
public class SetColor extends CommandBase {
  private final LedSubsystem led;
  private final ColorSensor colorSensor;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
 * @param colorSensor
   */
  public SetColor(LedSubsystem subsystem, ColorSensor colorSensor) {
    led = subsystem;
    this.colorSensor = colorSensor;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    led.setColor(BlinkinPatterns.PatternName.Color_Waves_Ocean_Palette);
    SmartDashboard.putString("Led", "value");
    SmartDashboard.putNumber("Sensor Red", colorSensor.getColor().red);
    SmartDashboard.putNumber("Sensor Green", colorSensor.getColor().green);
    SmartDashboard.putNumber("Sensor Blue", colorSensor.getColor().blue);
    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    led.off();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
