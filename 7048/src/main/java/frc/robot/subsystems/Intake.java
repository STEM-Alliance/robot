/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  //piston that moves up if we are going to get in a crash
  private DoubleSolenoid crash;
  //Takes balls in
  private WPI_TalonSRX intaker;
  private boolean isUp;
  public Intake() {
    //change device numbers
    crash=new DoubleSolenoid(44,43);
    intaker=new WPI_TalonSRX(99);
    
    isUp=false;
  }
  public void pushPiston(){
    crash.set(isUp?Value.kForward:Value.kReverse);
   //Make sure to change to the correct values not sure what it is yet but it is some combination of kForward,kReverse, and kOff
   //We also need 
  }
  public void intakeBalls(){}
  @Override
  public void periodic() {
    intaker.set(isUp?0.0:1.0);
  }
}
