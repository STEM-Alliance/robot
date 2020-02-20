/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;

public class Magazine extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  private WPI_TalonSRX magazineMover;
  private DoubleSolenoid stopper;
  private boolean stopperInPlace;
  public Magazine() {

    //Change Device Number
    magazineMover=new WPI_TalonSRX(Constants.magazineBeltNumber);
    stopper=new DoubleSolenoid(Constants.stopperNumbers[0],Constants.stopperNumbers[1]);
  }
  //make command
  public void moveBalls(){
    //change dis eventually
    magazineMover.set(Constants.magazineSpeed);
  }
  //not sure how to be worked yet
  public void setMagStopper(){
    stopper.set(stopperInPlace?Constants.stopperOff:Constants.stopperOn);

    
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
