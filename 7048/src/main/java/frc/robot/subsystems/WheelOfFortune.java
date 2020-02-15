/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WheelOfFortune extends SubsystemBase {
  /**
   * Control Panel for the uninitiated.
   */
  private WPI_TalonSRX spinnyMotor;
  public WheelOfFortune() {
    //please change number once we know the port
    spinnyMotor=new WPI_TalonSRX(42);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
