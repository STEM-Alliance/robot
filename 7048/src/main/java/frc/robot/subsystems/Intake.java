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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;

public class Intake extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  //piston that moves up if we are going to get in a crash
  private DoubleSolenoid crash;
  //Takes balls in
  private WPI_TalonSRX intaker;
  private boolean isUp;
  public static Value intakePistonOn=Value.kForward;
   public static Value intakePistonOff=Value.kReverse;
  public Intake() {
    //change device numbers
    crash=new DoubleSolenoid(Constants.intakePistonNumbers[0],Constants.intakePistonNumbers[1]);
    intaker=new WPI_TalonSRX(Constants.intakerNumber);
    crash.set(intakePistonOff);
    
  }
  public void pushPistonUp(){
    // crash.set(isUp?Constants.intakePistonOn:Constants.intakePistonOff);
   
    stopMotor();
    
    
      crash.set(intakePistonOff);
    

   //Make sure to change to the correct values not sure what it is yet but it is some combination of kForward,kReverse, and kOff
   //We also need 
   
  }
  public void pushPistonDown(){
    
    crash.set(intakePistonOn);
    intakeBalls();
    
  }
  //make command
  public void intakeBalls(){intaker.set(Constants.intakeSpeed);}
  public void stopMotor(){intaker.set(0.0);}
  @Override
  public void periodic() {
   
  }
}
