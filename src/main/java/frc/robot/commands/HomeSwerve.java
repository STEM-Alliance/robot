// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Configuration;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.math.controller.PIDController;

/** An example command that uses an example subsystem. */
public class HomeSwerve extends Command {
    final PIDController m_frontLeftPID = new PIDController(Configuration.kSwerveKp, Configuration.kSwerveKi, Configuration.kSwerveKd);
    //final PIDController m_frontRightPID = new PIDController(Configuration.kSwerveKp, Configuration.kSwerveKi, Configuration.kSwerveKd);
    //final PIDController m_backLeftPID = new PIDController(Configuration.kSwerveKp, Configuration.kSwerveKi, Configuration.kSwerveKd);
    //final PIDController m_backRightPID = new PIDController(Configuration.kSwerveKp, Configuration.kSwerveKi, Configuration.kSwerveKd);

    DrivetrainSubsystem m_driveTrain;
    /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   * @return 
   */
  public void HomeSwerve(DrivetrainSubsystem Drivetrain) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(Drivetrain);
    m_driveTrain = Drivetrain;
    m_frontLeftPID.setTolerance(Configuration.kSwerveZeroTolerancekP, Configuration.kSwerveZeroTolerancekD);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    var frontLeftModule = m_driveTrain.getModule(0);
    // var frontRightModule = DrivetrainSubsystem.getModule(1);
    //var backLeftModule = DrivetrainSubsystem.getModule(2);
    // var backRightModule = DrivetrainSubsystem.getModule(3);

    var frontLeftOutput = m_frontLeftPID.calculate(frontLeftModule.getAbsPos(), Configuration.kFrontLeftZero);
    // var frontRightOutput = m_frontLeftPID.calculate(frontRightModule.getAbsPos(), Configuration.kFrontRightZero);
    // var backLeftOutput = m_frontLeftPID.calculate(backLeftModule.getAbsPos(), Configuration.kBackLeftZero);
    // var backRightOutput = m_frontLeftPID.calculate(backRightModule.getAbsPos(), Configuration.kBackLeftZero);

    frontLeftModule.homeSwerve(frontLeftOutput);
    // frontRightModule.m_turningMotor.set(frontRightOutput);
    // backLeftModule.m_turningMotor.set(backLeftOutput);
    // backRightModule.m_turningMotor.set(backRightOutput);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_frontLeftPID.atSetpoint();
  }
}