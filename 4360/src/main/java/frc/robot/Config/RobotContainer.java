/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Config;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.DriveToTarget;
import frc.robot.commands.ExampleCommand;
import frc.robot.reuse.config.Xbox;
import frc.robot.reuse.config.HerdJoystick;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.Vision;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final Vision vision = new Vision();
  private final Drivetrain drivetrain  = new Drivetrain(this);
 
  private final DriveToTarget c_vision = new DriveToTarget(vision, drivetrain,this);
  private final ExampleCommand m_autoCommand = new ExampleCommand(m_exampleSubsystem,0);

  public Joystick go;
  public Joystick turn;  
  public XboxController xbox;
  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    xbox = new XboxController(0);
    //go = new Joystick(1);
    //turn = new Joystick(2);
    // Configure the button bindings
    configureButtonBindings();
  }


  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
 //JoystickButton visionbutton = new JoystickButton(xbox, Xbox.BUTTON.RB.get());
 //visionbutton.whenPressed(new DriveToTarget(vision, drivetrain,this));
    JoystickButton exampleButton = new JoystickButton(xbox, Xbox.BUTTON.A.get());
  exampleButton.whileHeld(new ExampleCommand(m_exampleSubsystem,0.5));
  
  
}


  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return m_autoCommand;
  }
}
