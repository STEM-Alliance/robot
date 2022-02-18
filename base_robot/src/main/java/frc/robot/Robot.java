// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.PWMTalonSRX;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.motorcontrol.Talon;

import javax.lang.model.util.ElementScanner6;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;


/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private XboxController m_xbox;

  private final MotorController m_leftMotor1 = new CANSparkMax(1, MotorType.kBrushless);
  private final MotorController m_leftMotor2 = new CANSparkMax(2, MotorType.kBrushless);
  private final MotorControllerGroup m_left = new MotorControllerGroup(m_leftMotor1, m_leftMotor2);

  private final MotorController m_rightMotor1 = new CANSparkMax(3, MotorType.kBrushless);
  private final MotorController m_rightMotor2 = new CANSparkMax(4, MotorType.kBrushless);
  private final MotorControllerGroup m_right = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);

  private final MotorController m_launcher = new CANSparkMax(5, MotorType.kBrushless);

  private final TalonSRX m_harvester = new TalonSRX(10);
  private final TalonSRX m_climber = new TalonSRX(11);
  private final TalonSRX m_indexer = new TalonSRX(12);
  private final TalonSRX m_arm = new TalonSRX(13);

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_right.setInverted(true);

    m_myRobot = new DifferentialDrive(m_left, m_right);
    m_myRobot.setDeadband(0.1);
    m_xbox = new XboxController(0);

    System.out.println("Robot starting");
  }

  @Override
  public void teleopPeriodic() {
    m_myRobot.arcadeDrive(m_xbox.getLeftY(), m_xbox.getLeftX());
    
    if (m_xbox.getAButton())
    {
      m_launcher.set(0.8);
      System.out.println("Launcher to 80%");
    }
    else if (m_xbox.getBButton())
    {
      m_launcher.set(0.6);
      System.out.println("Launcher to 60%");
    }
    else if (m_xbox.getXButton())
    {
      m_launcher.set(0.4);
      System.out.println("Launcher to 40%");
    }
    else
    {
      m_launcher.set(0);
    }

    if (m_xbox.getLeftBumper())
    {
      m_harvester.set(TalonSRXControlMode.PercentOutput, 1.0);
    }
    else
    {
      m_harvester.set(TalonSRXControlMode.PercentOutput, 0);
    }
    if (m_xbox.getRightBumper())
    {
      m_climber.set(TalonSRXControlMode.PercentOutput, 1.0);
    }
    else
    {
      m_climber.set(TalonSRXControlMode.PercentOutput, 0);
    }
    if (m_xbox.getLeftTriggerAxis() > 0.5)
    {
      m_indexer.set(TalonSRXControlMode.PercentOutput, 1.0);
    }
    else
    {
      m_indexer.set(TalonSRXControlMode.PercentOutput, 0);
    }
    if (m_xbox.getRightTriggerAxis() > 0.5)
    {
      m_arm.set(TalonSRXControlMode.PercentOutput, 1.0);
    }
    else
    {
      m_arm.set(TalonSRXControlMode.PercentOutput, 0);
    }
  }
}
