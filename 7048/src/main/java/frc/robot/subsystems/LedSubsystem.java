/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.reuse.hardware.Blinkin;
import frc.robot.reuse.hardware.lowleveldriver.BlinkinPatterns.PatternName;

public class LedSubsystem extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  Blinkin led;
  public LedSubsystem() {
    led = new Blinkin(0, PatternName.Color_1_Light_Chase);
  }
  public void setColor() {
   led.signalDriveTeam();
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
  public void off() {
    led.off();
  }
}
