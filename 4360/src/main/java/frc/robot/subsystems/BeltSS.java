/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.BeltC;

public class BeltSS extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
 private WPI_VictorSPX motor;
 
  public BeltSS(Joystick joystick) {
  motor = new WPI_VictorSPX(19);
  setDefaultCommand(new BeltC(this, joystick));
  }
  public void move(double input){
    motor.set(ControlMode.PercentOutput, input);
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

}
