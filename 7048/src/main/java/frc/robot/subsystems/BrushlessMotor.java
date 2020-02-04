/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.BrushlessCommand;

public class BrushlessMotor extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  private CANSparkMax spark;
  public BrushlessMotor() {
    // BrushlessCommand brushlesscommand = new BrushlessCommand(this);
    // setDefaultCommand(brushlesscommand);
    spark = new CANSparkMax(9, MotorType.kBrushless);
  }
  public void driveNeo( double speed){
    spark.set(speed);
   }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
