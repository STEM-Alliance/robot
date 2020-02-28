/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;

public class RaiseHook extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  enum HookPosition {
    UP,
    MIDDLE,
    DOWN
  };
  private DoubleSolenoid raisePiston1;
  private DoubleSolenoid raisePiston2;
  private HookPosition position;
  private boolean up;
  
  public RaiseHook() {
    position = HookPosition.DOWN;
    raisePiston1 = new DoubleSolenoid(1, Constants.hookOne[0], Constants.hookOne[1]);
    raisePiston2 = new DoubleSolenoid(1, Constants.hookTwo[0], Constants.hookTwo[1]);
    raisePiston1.set(DoubleSolenoid.Value.kReverse);
    raisePiston2.set(DoubleSolenoid.Value.kReverse);
    up = false;
  }

  public void hookUp() {
    if(position==HookPosition.MIDDLE){
      raisePiston2.set(DoubleSolenoid.Value.kForward);
      position=HookPosition.UP;
    } else {
      raisePiston1.set(DoubleSolenoid.Value.kForward);
      position=HookPosition.MIDDLE;
    }
  }
  public void hookDown(){
    raisePiston1.set(DoubleSolenoid.Value.kReverse);
    raisePiston2.set(DoubleSolenoid.Value.kReverse);
    position=HookPosition.DOWN;
  }
  
    
  

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
