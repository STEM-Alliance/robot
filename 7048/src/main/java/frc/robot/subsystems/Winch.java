/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;

public class Winch extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  private CANSparkMax spark;

  public Winch() {
    spark = new CANSparkMax(Constants.winchNumber, MotorType.kBrushless);

  }
  public void SpinWinch(double speed){
    spark.set(speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
