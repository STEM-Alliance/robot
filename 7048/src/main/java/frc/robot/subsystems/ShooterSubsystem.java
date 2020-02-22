/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.SparkMax;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;
import frc.robot.reuse.hardware.sensors.GyroNavx;


public class ShooterSubsystem extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  //Used to find angle of aimMotor
   private GyroNavx euro;
  private Relay aimMotor;
  // Might be using the wrong class for the spark
  private Spark shootyMotor;
  //make sure to set this value
  private double shooterSpeed;
  private Value aimMotorClockwiseDirection;
  private Value aimMotorCCWDirection;
  public ShooterSubsystem() {
    //Please Change these constructor values to the correct one
    aimMotor=new Relay(Constants.aimMotorNumber);
    shootyMotor=new Spark(Constants.shootMotorNumber);
    euro=new GyroNavx();
    aimMotor.set(Value.kOn);

  }
  public void shoot(){
    shootyMotor.set(Constants.shootMotorSpeed);

  }
  public void stopShooter(){
    shootyMotor.set(0.0);
  }
  public void moveAimMotorClockwise(){
    aimMotor.set(aimMotorClockwiseDirection);
  }
  public void moveAimMotorCCW(){
    aimMotor.set(aimMotorCCWDirection);
  }
  public void stopAimMotor(){
    aimMotor.set(Value.kOn);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
