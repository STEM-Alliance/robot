// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.AnalogInput;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
import frc.robot.Configuration;

public class DriveSubSystem extends SubsystemBase {
    private AnalogInput m_swervePos1 = new AnalogInput(0);
    private AnalogInput m_swervePos2 = new AnalogInput(1);
    private AnalogInput m_swervePos3 = new AnalogInput(2);
    private AnalogInput m_swervePos4 = new AnalogInput(3);
    //private CANSparkMax m_drive1 = new CANSparkMax(0, MotorType.kBrushless);
    //private CANSparkMax m_swerve1 = new CANSparkMax(1, MotorType.kBrushless);
    // private CANSparkMax m_drive2 = new CANSparkMax(2, MotorType.kBrushless);
    // private CANSparkMax m_swerve2 = new CANSparkMax(3, MotorType.kBrushless);
    // private CANSparkMax m_drive3 = new CANSparkMax(4, MotorType.kBrushless);
    // private CANSparkMax m_swerve3 = new CANSparkMax(5, MotorType.kBrushless);
    // private CANSparkMax m_drive4 = new CANSparkMax(6, MotorType.kBrushless);
    // private CANSparkMax m_swerve4 = new CANSparkMax(7, MotorType.kBrushless);

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
    //m_drive1.setSmartCurrentLimit(Configuration.NeoLimit);
    //m_swerve1.setSmartCurrentLimit(Configuration.NeoLimit);
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
    // Example chassis speeds: 1 meter per second forward, 3 meters
    // per second to the left, and rotation at 1.5 radians per second
    // counterclockwise.
    ChassisSpeeds speeds = new ChassisSpeeds(vx, vy, omega);

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

    var str = "Speed: " + frontLeft.speedMetersPerSecond + " angles: ";
    for (int i = 0; i < 4; i++)
    {
        str += moduleStates[i].angle.getDegrees() + "/";
    }
    System.out.println(str);
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
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
