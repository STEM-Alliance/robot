/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LoadingSubsystem extends SubsystemBase {

  private WPI_TalonSRX loadMotor;
  /**
   * Creates a new ExampleSubsystem.
   */
 
  public LoadingSubsystem() {

    loadMotor = new WPI_TalonSRX(19);
    loadMotor.setInverted(true);
  }

  public void load(double loadSpeed){
    loadMotor.set(loadSpeed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

}
