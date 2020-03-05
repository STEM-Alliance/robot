/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;
import frc.robot.commands.MoveMagazineBalls;
import frc.robot.reuse.config.Xbox;

public class Magazine extends SubsystemBase {
  /**
   * Creates a new ExampleSubsystem.
   */
  private WPI_VictorSPX magazineMover;
  private DoubleSolenoid stopper;
  private boolean stopperInPlace;
  public static Value stopperOn=Value.kForward;
  public static Value stopperOff=Value.kReverse;
  public XboxController xbox;

  public Magazine(XboxController xboxIn) {

    //Change Device Number
    SmartDashboard.putString("magazine", "made");
    magazineMover=new WPI_VictorSPX(Constants.magazineBeltNumber);
    stopper=new DoubleSolenoid(1, Constants.stopperNumbers[0],Constants.stopperNumbers[1]);
    xbox = xboxIn;
    setDefaultCommand(new MoveMagazineBalls(this));

  }
  //make command
  public void moveBalls(){
    SmartDashboard.putNumber("MagNumber", xbox.getRawAxis(Xbox.AXIS.RIGHT_TRIGGER.get())-xbox.getRawAxis(Xbox.AXIS.LEFT_TRIGGER.get()));
    magazineMover.set(xbox.getRawAxis(Xbox.AXIS.RIGHT_TRIGGER.get())-xbox.getRawAxis(Xbox.AXIS.LEFT_TRIGGER.get()));
  }
  
 
  //not sure how to be worked yet
  public void setMagStopperInPlace(){
    //stopper.set(stopperOn);
  }
  public void setMagStopperOutOfPlace(){
    stopper.set(stopperOff);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
