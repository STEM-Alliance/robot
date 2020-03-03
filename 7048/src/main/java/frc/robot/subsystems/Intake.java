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
  //piston that moves up if we are going to get in a armPiston
  private DoubleSolenoid intakePiston;
  //Takes balls in
  private WPI_TalonSRX intakeMotor;
  private boolean isUp;
  public static Value intakePistonOn=Value.kForward;
   public static Value intakePistonOff=Value.kReverse;
  public Intake() {
    //change device numbers
    intakePiston=new DoubleSolenoid(1, Constants.intakePistonNumbers[0],Constants.intakePistonNumbers[1]);
    intakeMotor=new WPI_TalonSRX(Constants.intakerNumber);
    intakePiston.set(intakePistonOff);
    
  }
  public void pushPistonUp(){
    // armPiston.set(isUp?Constants.intakePistonOn:Constants.intakePistonOff);
    SmartDashboard.putString("intake piston", "piston is up");
    stopMotor();
    
    
      intakePiston.set(intakePistonOff);
    

   //Make sure to change to the correct values not sure what it is yet but it is some combination of kForward,kReverse, and kOff
   //We also need 
   
  }
  public void pushPistonDown(){
    
    intakePiston.set(intakePistonOn);
    intakeBalls();
    SmartDashboard.putString("intake piston", "piston is down");
    
  }
  //make command
  public void intakeBalls(){
    intakeMotor.set(Constants.intakeSpeed);
  }
  public void stopMotor(){
    intakeMotor.set(0.0);
  }
  @Override
  public void periodic() {
   
  }
}
