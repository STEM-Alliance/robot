/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.Config.RobotContainer;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.ExampleSubsystem;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * An example command that uses an example subsystem.
 */
public class auto extends CommandBase {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final Drivetrain m_subsystem;

  private double speed;
  private Timer m_timer = new Timer();
  private double time;
  public DifferentialDrive robotDrive;
  private RobotContainer container;
 
  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public auto( double speed, double time) {
    m_subsystem = new Drivetrain(container);
   this.speed = speed;
   this.time = time;
    //this.speed = speed;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_subsystem);
    

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    
    m_timer.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
   
    m_subsystem.robotDrive.tankDrive(speed , speed, true);
    SmartDashboard.putNumber("timer", m_timer.get());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_subsystem.robotDrive.tankDrive(0 , 0, true);

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if(m_timer.get() >= time){
        return true;
    }else return false;
    //return false;
  }
}
