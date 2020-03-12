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

public class ShooterSubsystem extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  
  // Might be using the wrong class for the spark
  private CANSparkMax shootyMotor;
  
  public ShooterSubsystem() {
    //Please Change these constructor values to the correct one
    
    shootyMotor=new CANSparkMax(Constants.shootMotorNumber, MotorType.kBrushless);
    // euro=new GyroNavx();

  }
  public void shoot(){
    shootyMotor.set(Constants.shootMotorSpeed);

  }
  public void stopShooter(){
    shootyMotor.set(0.0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
