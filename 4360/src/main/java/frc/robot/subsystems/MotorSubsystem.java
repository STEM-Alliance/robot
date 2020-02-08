/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.RobotContainer;
import frc.robot.commands.Shooter;
import frc.robot.reuse.config.Xbox;

public class MotorSubsystem extends SubsystemBase {

  private WPI_TalonSRX motor1;
  private WPI_TalonSRX motor2;
  private RobotContainer contain;

  /**
   * Creates a new ExampleSubsystem.
   */
 
  public MotorSubsystem(RobotContainer contain) {
    this.contain = contain;
    motor1 = new WPI_TalonSRX(8);
    motor2 = new WPI_TalonSRX(10);
    motor2.follow(motor1);
   // motor2.setInverted(true);
    setDefaultCommand(new Shooter(this));
    motor1.setInverted(true);
  }
  public void shoot(){
    
    motor1.set(ControlMode.PercentOutput , contain.xbox.getRawAxis(Xbox.AXIS.RIGHT_TRIGGER.get()));
   

  }

 
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

}
