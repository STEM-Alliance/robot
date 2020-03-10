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

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;
import frc.robot.commands.togglespeed;

public class ShooterSS extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
public static double togglespeed;
 private WPI_TalonSRX motor;
 private WPI_TalonSRX motor2;
  public ShooterSS() {
  motor = new WPI_TalonSRX(1);
  motor2 = new WPI_TalonSRX(2);

  motor2.follow(motor);
  }
  public void move(double input){
    motor.set(ControlMode.PercentOutput, input);
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

}
