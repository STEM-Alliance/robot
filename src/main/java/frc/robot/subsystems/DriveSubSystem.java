// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
import frc.robot.Configuration;
import java.math.*;
import frc.robot.LoggedNumber;

public class DriveSubSystem extends SubsystemBase {
    private double m_vx = 0.0;
    private double m_vy = 0.0;
    private double m_omega = 0.0;

    private AnalogInput m_swervePos1 = new AnalogInput(0);
    private AnalogInput m_swervePos2 = new AnalogInput(1);
    private AnalogInput m_swervePos3 = new AnalogInput(2);
    private AnalogInput m_swervePos4 = new AnalogInput(3);
    private double m_prevServerPos1 = 0;
    private CANSparkMax m_drive1 = new CANSparkMax(1, MotorType.kBrushless);
    private CANSparkMax m_swerve1 = new CANSparkMax(2, MotorType.kBrushless);
    // private CANSparkMax m_drive2 = new CANSparkMax(2, MotorType.kBrushless);
    // private CANSparkMax m_swerve2 = new CANSparkMax(3, MotorType.kBrushless);
    // private CANSparkMax m_drive3 = new CANSparkMax(4, MotorType.kBrushless);
    // private CANSparkMax m_swerve3 = new CANSparkMax(5, MotorType.kBrushless);
    // private CANSparkMax m_drive4 = new CANSparkMax(6, MotorType.kBrushless);
    // private CANSparkMax m_swerve4 = new CANSparkMax(7, MotorType.kBrushless);

    private PIDController m_pid1 = new PIDController(0.005, 0, 0);

    // Locations for the swerve drive modules relative to the robot center.
    Translation2d m_frontLeftLocation = new Translation2d(0.381, 0.381);
    Translation2d m_frontRightLocation = new Translation2d(0.381, -0.381);
    Translation2d m_backLeftLocation = new Translation2d(-0.381, 0.381);
    Translation2d m_backRightLocation = new Translation2d(-0.381, -0.381);

    // Creating my kinematics object using the module locations
    SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
    m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation
    );    

  /** Creates a new DriveSubSystem. */
  public DriveSubSystem() {
        // Setup current limits
    m_drive1.setSmartCurrentLimit(Configuration.NeoLimit);
    m_swerve1.setSmartCurrentLimit(Configuration.NeoLimit);
    // m_drive2.setSmartCurrentLimit(Configuration.NeoLimit);
    // m_swerve2.setSmartCurrentLimit(Configuration.NeoLimit);
    // m_drive3.setSmartCurrentLimit(Configuration.NeoLimit);
    // m_swerve3.setSmartCurrentLimit(Configuration.NeoLimit);
    // m_drive4.setSmartCurrentLimit(Configuration.NeoLimit);
    // m_swerve4.setSmartCurrentLimit(Configuration.NeoLimit);
  }

  /**
   * Example command factory method.
   *
   * @return a command
   */
  public void swerveControl(double vx, double vy, double omega) {
    m_vx = vx;
    m_vy = vy;
    m_omega = omega;
}

  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    ChassisSpeeds speeds = new ChassisSpeeds(m_vx, m_vy, m_omega);

    // Convert to module states
    SwerveModuleState[] moduleStates = m_kinematics.toSwerveModuleStates(speeds);

    // Front left module state
    SwerveModuleState frontLeft = moduleStates[0];
    // Front right module state
    SwerveModuleState frontRight = moduleStates[1];
    // Back left module state
    SwerveModuleState backLeft = moduleStates[2];
    // Back right module state
    SwerveModuleState backRight = moduleStates[3];  

    double desiredpos1 = moduleStates[0].angle.getDegrees();
    double swervePos1 = m_swervePos1.getAverageValue();
    // Convert analog voltage to degrees.
    var currentPosDegrees = (swervePos1 / 4092) * 360;
    var robotPosDegrees = currentPosDegrees - 180;
    SwerveModuleState frontLeftOpt = SwerveModuleState.optimize(moduleStates[0], Rotation2d.fromDegrees(robotPosDegrees));
    
    // Feed error term to PID controller
    var output1 = m_pid1.calculate(robotPosDegrees, frontLeftOpt.angle.getDegrees()); 

    // Feed output of PID to motor
    m_swerve1.set(output1);
    m_drive1.set(frontLeftOpt.speedMetersPerSecond / 10);

    SmartDashboard.putNumber("Analog", swervePos1);
    SmartDashboard.putNumber("Desired", desiredpos1);
    SmartDashboard.putNumber("RobotPosDegrees", robotPosDegrees);
    SmartDashboard.putNumber("pidOut", output1);

    LoggedNumber.getInstance().logNumber("Analog", swervePos1);
    LoggedNumber.getInstance().logNumber("Desired", desiredpos1);
    LoggedNumber.getInstance().logNumber("RobotPosDegrees", robotPosDegrees);
    LoggedNumber.getInstance().logNumber("pidOut", output1);
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
