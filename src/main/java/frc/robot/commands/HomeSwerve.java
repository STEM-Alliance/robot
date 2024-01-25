// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Configuration;
import frc.robot.subsystems.DrivetrainSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

import java.util.Timer;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

// import com.revrobotics.CANSparkLowLevel.FollowConfig.Config;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** An example command that uses an example subsystem. */

public class HomeSwerve extends Command {
    private final DrivetrainSubsystem m_driveTrain;
    int m_counter = 0;
    
    final PIDController m_frontLeftPID = new PIDController(
      Configuration.kSwerveZeroPIDKp, Configuration.kSwerveZeroPIDKi, Configuration.kSwerveZeroPIDKd);

    final PIDController m_frontRightPID = new PIDController(
      Configuration.kSwerveZeroPIDKp, Configuration.kSwerveZeroPIDKi, Configuration.kSwerveZeroPIDKd);

    final PIDController m_backLeftPID = new PIDController(
      Configuration.kSwerveZeroPIDKp, Configuration.kSwerveZeroPIDKi, Configuration.kSwerveZeroPIDKd);

    final PIDController m_backRightPID = new PIDController(
      Configuration.kSwerveZeroPIDKp, Configuration.kSwerveZeroPIDKi, Configuration.kSwerveZeroPIDKd);
    
    private boolean frontLeftReady;
    private boolean frontRightReady;
    private boolean backLeftReady;
    private boolean backRightReady;
    
    

  
    /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   * @return 
   */
  public HomeSwerve(DrivetrainSubsystem Drivetrain) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(Drivetrain);
    m_driveTrain = Drivetrain;

    
    m_frontLeftPID.enableContinuousInput(0, 4092);
    m_frontLeftPID.setTolerance(Configuration.kSwerveZeroToleranceKp, Configuration.kSwerveZeroToleranceKd);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    var frontLeftModule = m_driveTrain.getModule(0);
    var frontRightModule = m_driveTrain.getModule(1);
    var backLeftModule = m_driveTrain.getModule(2);
    var backRightModule = m_driveTrain.getModule(3);

    var frontLeftOutput = m_frontLeftPID.calculate(frontLeftModule.getAbsPos(), Configuration.kFrontLeftZero);
    var frontRightOutput = m_frontLeftPID.calculate(frontRightModule.getAbsPos(), Configuration.kFrontRightZero);
    var backLeftOutput = m_frontLeftPID.calculate(backLeftModule.getAbsPos(), Configuration.kBackLeftZero);
    var backRightOutput = m_frontLeftPID.calculate(backRightModule.getAbsPos(), Configuration.kBackLeftZero);
   
   
    frontLeftModule.homeSwerve(frontLeftOutput);
    frontRightModule.homeSwerve(frontRightOutput);
    backLeftModule.homeSwerve(backLeftOutput);
    backRightModule.homeSwerve(backRightOutput);
    
    SmartDashboard.putNumber("ZeroPos", frontLeftModule.getAbsPos());
    SmartDashboard.putNumber("ZeroPID", frontLeftOutput);
    SmartDashboard.putNumber("ZeroCounter", m_counter++);

     if (frontLeftModule.getAbsPos() >= (Configuration.kFrontLeftZero - 5) &  frontLeftModule.getAbsPos() <= (Configuration.kFrontLeftZero + 5)){
        frontLeftReady = true;
    }
     if (frontRightModule.getAbsPos() >= (Configuration.kFrontRightZero - 5) &  frontRightModule.getAbsPos() <= (Configuration.kFrontRightZero + 5)){
        frontRightReady = true;
    }
     if (backLeftModule.getAbsPos() >= (Configuration.kBackLeftZero - 5) &  backLeftModule.getAbsPos() <= (Configuration.kBackLeftZero + 5)){
        backLeftReady = true;
    }
     if (backRightModule.getAbsPos() >= (Configuration.kBackRightZero - 5) &  backRightModule.getAbsPos() <= (Configuration.kBackRightZero + 5)){
        backRightReady = true;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    
      var frontLeftModule = m_driveTrain.getModule(0);
    var frontRightModule = m_driveTrain.getModule(1);
    var backLeftModule = m_driveTrain.getModule(2);
    var backRightModule = m_driveTrain.getModule(3);

    boolean ret = frontLeftReady == true && frontRightReady == true && backLeftReady == true && backRightReady == true;
    if (ret)
    { 
       frontLeftModule.doneHoming();
      frontRightModule.doneHoming();
      backLeftModule.doneHoming();
      backRightModule.doneHoming();
    }
    return ret;
  }
  
}