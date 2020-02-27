/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.fasterxml.jackson.annotation.JacksonInject.Value;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;
import frc.robot.commands.Aim;
import frc.robot.reuse.config.Xbox;

public class AimMotor extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  private Relay aimMotor;
  private XboxController xbox;
  public AimMotor(XboxController xbox) {
    aimMotor=new Relay(Constants.aimMotorNumber);
    setDefaultCommand(new Aim(this));
    this.xbox = xbox;
  }
  public void moveAimMotorClockwise(){
    aimMotor.set(Relay.Value.kForward);
    if(xbox.getRawAxis(Xbox.AXIS.RIGHT_Y.get()) > 0.5){
      aimMotor.set(Relay.Value.kForward);
    }
    else if(xbox.getRawAxis(Xbox.AXIS.RIGHT_Y.get()) < -0.5){
      aimMotor.set(Relay.Value.kReverse);
    }
  }
  // public void moveAimMotorCCW(){
  //   aimMotor.set(Relay.Value.kReverse);
  //}
  public void stopAimMotor(){
    aimMotor.set(Relay.Value.kOff);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

}
