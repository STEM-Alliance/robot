package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.revrobotics.*;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.lang.Math;

import edu.wpi.first.cameraserver.CameraServer;
import java.nio.file.Path;
import java.util.function.DoubleBinaryOperator;

import edu.wpi.first.math.trajectory.*;
import java.io.IOException;
import com.kauailabs.navx.frc.*;
import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.math.geometry.*;

public class Testboard extends TimedRobot {
    //private DifferentialDrive m_myRobot;
    private XboxController m_controller1;
    private XboxController m_controller2;
  
    //Sets the motor ids and names
    private final CANSparkMax Spark1 = new CANSparkMax(1, MotorType.kBrushless);
    private final CANSparkMax Spark2 = new CANSparkMax(2, MotorType.kBrushless);
    private final TalonSRX Talon1 = new TalonSRX(12);
    private final TalonSRX Talon2 = new TalonSRX(13);
    

   
// robot init is the robot starting up
// the controllers are assigned here 
    @Override
  public void robotInit() {
    m_controller1 = new XboxController(0);
    m_controller2 = new XboxController(1);
  
  
    //Comment to set the sparks to brake also uncomment lines 56,57
    Spark1.setIdleMode(IdleMode.kCoast);
    Spark2.setIdleMode(IdleMode.kCoast);
    
    //Uncomment to set the sparks to brake
    //Spark1.setIdleMode(IdleMode.Brake);
    //Spark2.setIdleMode(IdleMode.Brake);
 }


 //begining of teleop code
  @Override
  public void teleopPeriodic() {
  double Spark1Speed = m_controller1.getLeftY();
  double Spark2Speed = m_controller1.getRightY();
  double Talon1Speed = m_controller2.getLeftY();
  Double Talon2Speed = m_controller2.getRightY();
  // Using if statements to set motor speed
  // These if statements are for the Spark controllers 
  if (Spark1Speed > 0.1) {
        Spark1.set(Spark1Speed);
    }
    else if (Spark1Speed < -0.1) {
        Spark1.set(Spark1Speed);
    }
    else {
        Spark1.set(0);
    }
   if (Spark2Speed > 0.1) {
        Spark2.set(Spark2Speed);
    }
    else if (Spark2Speed < -0.1) {
        Spark2.set(Spark2Speed);
    }
    else {
        Spark2.set(0);
    }
   
   // these if statements are for the TalonSRX controllers
    if (Talon1Speed > 0.1){
        Talon1.set(TalonSRXControlMode.PercentOutput, Talon1Speed);
    }
    else if (Talon1Speed < -0.1){
        Talon1.set(TalonSRXControlMode.PercentOutput, Talon1Speed);
    }
    else {
        Talon1.set(TalonSRXControlMode.PercentOutput, 0);
    }

    if (Talon2Speed > 0.1){
        Talon2.set(TalonSRXControlMode.PercentOutput, Talon2Speed);
    }
    else if (Talon2Speed < -0.1){
        Talon2.set(TalonSRXControlMode.PercentOutput, Talon2Speed);
    }
    else {
        Talon2.set(TalonSRXControlMode.PercentOutput, 0);
    }
  }
}
