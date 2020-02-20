/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;

public class WheelMotor extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  private WPI_TalonSRX spinnyMotor;
  
  public WheelMotor() {
    //Change number
    spinnyMotor=new WPI_TalonSRX(Constants.pizzaMotorNumber);

  }
 public void setMotor(){
   spinnyMotor.set(Constants.pizzaMotorSpeed);
 }
 public void stopMotor(){
   spinnyMotor.set(0.0);
   
 }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
