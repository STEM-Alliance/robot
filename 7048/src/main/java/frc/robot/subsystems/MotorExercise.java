/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.MotorCommand;

public class MotorExercise extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */

  private WPI_TalonSRX talon;

  public MotorExercise() {
    MotorCommand motorCommand = new MotorCommand(this);
    setDefaultCommand(motorCommand);
    talon = new WPI_TalonSRX(10);

  }
  public void driveFoward(){
   talon.set(1.0);
  }




  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
