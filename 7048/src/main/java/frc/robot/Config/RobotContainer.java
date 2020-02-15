/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Config;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.DriveToTarget;
import frc.robot.commands.PistonUp;
import frc.robot.commands.SetColor;
import frc.robot.commands.SignalHuman;
import frc.robot.commands.turnWheelRotations;
import frc.robot.reuse.config.Xbox;
import frc.robot.subsystems.ColorSensor;
import frc.robot.subsystems.ShooterSubsystem;
//import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.LedSubsystem;
import frc.robot.subsystems.Vision;
import frc.robot.subsystems.WheelMotor;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Magazine;
import frc.robot.subsystems.WheelOfFortune;;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final LedSubsystem ledSubsystem = new LedSubsystem();
  private final ColorSensor colorSensor = new ColorSensor();
  private final Drivetrain drivetrain = new Drivetrain(this);
  private final ShooterSubsystem shooter=new ShooterSubsystem();
  private final Intake intake=new Intake();
  private final Magazine magazine=new Magazine();
  private final WheelMotor wheelMotor=new WheelMotor();
  private final WheelOfFortune controlPanel=new WheelOfFortune(wheelMotor,colorSensor);

  //private final Drivetrain driveSubsystem = new Drivetrain(this);
  private final Vision vision = new Vision();


  public XboxController xbox;
  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    xbox = new XboxController(0);
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
    //figure out how controller wants things controlled
    JoystickButton ledButton = new JoystickButton(xbox, Xbox.BUTTON.X.get());
    ledButton.whileHeld(new SetColor(ledSubsystem, colorSensor));

    JoystickButton visonDrive = new JoystickButton(xbox, Xbox.BUTTON.RB.get());
    visonDrive.whileHeld(new DriveToTarget(vision, drivetrain, this));

    JoystickButton pistonUp=new JoystickButton(xbox, Xbox.BUTTON.LB.get());
    pistonUp.whenPressed(new PistonUp(intake));

    JoystickButton turner=new JoystickButton(xbox,Xbox.BUTTON.LEFT_STICK.get());
    turner.whileHeld(new turnWheelRotations(controlPanel));
  }


  /**
   * 
   * This is where you load in your auto command!
   */
  private final SetColor autoCommand = new SetColor(ledSubsystem, colorSensor);

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return autoCommand;
  }
}
