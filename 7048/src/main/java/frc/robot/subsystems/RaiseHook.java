/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;

public class RaiseHook extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
   private DoubleSolenoid raisePiston;
   private  boolean up;
  public RaiseHook() {
   up = true;
    raisePiston =  new DoubleSolenoid(Constants.hook[0], Constants.hook[1]);
  }

  public void raise(){ 
    if(up){
    raisePiston.set(Constants.hookOffValue);
  }
  else{
   raisePiston.set(Constants.hookOnValue) ;
  }
}
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
