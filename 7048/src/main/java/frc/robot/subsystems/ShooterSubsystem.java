/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.SparkMax;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.reuse.hardware.sensors.GyroNavx;

public class ShooterSubsystem extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  //Used to find angle of aimMotor
   private GyroNavx euro;
  private WPI_TalonSRX aimMotor;
  // Might be using the wrong class for the spark
  private Spark shootyMotor;
  private double shooterSpeed;
  public ShooterSubsystem() {
    //Please Change these constructor values to the correct one
    aimMotor=new WPI_TalonSRX(69);
    shootyMotor=new Spark(420);
    euro=new GyroNavx();

  }
  public void Shoot(){
    shootyMotor.set(shooterSpeed);
  }


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
