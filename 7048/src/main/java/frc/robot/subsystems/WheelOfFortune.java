/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WheelOfFortune extends SubsystemBase {
  /**
   * Control Panel for the uninitiated.
   */
  //Change if wanted
  private double numOfRotations=3.0;
  
  private Color coloration;
  private int rotationsAccomplished=0;
  //subsystem used to run the motor
  private WheelMotor wheelSubsystem;
  //used to read the color
  private ColorSensor sensor;
  public WheelOfFortune(WheelMotor wheely,ColorSensor sensor) {
    wheelSubsystem=wheely;
    this.sensor=sensor;
    
  }
   public void runTurnsForRotation(){
     //change value as appropriate
    wheelSubsystem.setMotor(1.0);
    if(sensor.getColor()!=coloration){
      rotationsAccomplished+=0.125;
    }
    coloration=sensor.getColor();
   }
   public boolean reachedRotations(){
     return rotationsAccomplished>=numOfRotations;
   }
   public void stopMotor(){
     wheelSubsystem.setMotor(0.0);

   }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
