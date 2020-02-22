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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WheelOfFortune extends SubsystemBase {
  /**
   * Control Panel for the uninitiated.
   */
  //Change if wanted
  /**Colors Blue G255 B255 R0
   * Green G255 B0 R0
   * Yellow G255 B0 R255
   * Red G0 B0 R255
  */
  private boolean colorSet;
  public enum Colors{
    YELLOW,RED,GREEN,BLUE
    
  }
  private double numOfRotations=4.0;
  
  private Colors previousColor;
  private double rotationsAccomplished=0;
  //subsystem used to run the motor
  private WheelMotor wheelSubsystem;
  //used to read the color
  private ColorSensor sensor;
  //not sure how we will get that yet
  private Colors mainColor;
  private Colors currentColor;

  public WheelOfFortune(WheelMotor wheely,ColorSensor sensor) {
    wheelSubsystem=wheely;
    this.sensor=sensor;
    
    
  }
   public void runTurnsForRotation(){
     //change value as appropriate
    wheelSubsystem.setMotor();
    if(currentColor!=previousColor){
      rotationsAccomplished+=0.125;
      SmartDashboard.putString("Number", Double.toString(rotationsAccomplished)+" rotations have been accomplished");
    }
    previousColor=currentColor;
   }
   public boolean reachedRotations(){
    SmartDashboard.putString("rots", "Rotations have been reached");
     return rotationsAccomplished>=numOfRotations;
     
   }
   public void stopMotor(){
     wheelSubsystem.stopMotor();

   }
  @Override
  public void periodic() {
    updateColor();
  }
  public void setTheMotor(){
    wheelSubsystem.setMotor();
  }
  /**Colors Blue G255 B255 R0
   * Green G255 B0 R0
   * Yellow G255 B0 R255
   * Red G0 B0 R255
  */
  public boolean toColor(){
    return colorSet;
  }
  public void updateColor(){
    //Color sensorColor=sensor.getColor();
    Color sensorColor = sensor.getColor();
    if(sensorColor.blue>=250.0){
      currentColor=Colors.BLUE;
    }
    else if(sensorColor.green<=5.0){
      currentColor=Colors.RED;
    }
    else if(sensorColor.red<=5.0){
      currentColor=Colors.GREEN;
    }
    else{
      currentColor=Colors.YELLOW;
    }
    
  }
  //add a command for this
  public void setToColor(){
     if(currentColor.equals(mainColor)){
      wheelSubsystem.stopMotor();colorSet=true;
     }
     else{
       wheelSubsystem.setMotor();
     }

  }
}
