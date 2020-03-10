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
import frc.robot.commands.BeltC;
import frc.robot.commands.DriveTeleop;
import frc.robot.commands.DriveToTarget;

import frc.robot.commands.ShooterC;
import frc.robot.commands.WinchC;
import frc.robot.commands.auto;
import frc.robot.commands.elevatorC;
import frc.robot.commands.togglespeed;
import frc.robot.reuse.config.Xbox;
import frc.robot.reuse.config.HerdJoystick;
import frc.robot.subsystems.BeltSS;
import frc.robot.subsystems.Drivetrain;

import frc.robot.subsystems.IntakeSS;
import frc.robot.subsystems.PizzaSS;
import frc.robot.commands.IntakeC;
import frc.robot.commands.PizzaC;
import frc.robot.subsystems.ShooterSS;
import frc.robot.subsystems.Vision;
import frc.robot.subsystems.Winch;
import frc.robot.subsystems.elevatorSS;
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
  private final elevatorSS m_exampleSubsystem = new elevatorSS();
  private final ShooterSS shootersystem = new ShooterSS();
  private final IntakeSS intakesystem = new IntakeSS();
  private final BeltSS beltsystem;
  private final Winch winchsystem = new Winch();
  private final Vision vision = new Vision();
  private final Drivetrain drivetrain  = new Drivetrain(this);
 private final DriveTeleop driveTeleop = new DriveTeleop(drivetrain, this,0);
  private final DriveToTarget c_vision = new DriveToTarget(vision, drivetrain,this);
 private final PizzaSS pizzasystem = new PizzaSS();
private final ShooterC shoot = new ShooterC(shootersystem, 0);
private final IntakeC intake = new IntakeC(intakesystem,0);
private final WinchC winch = new WinchC(winchsystem, 0);
  public Joystick go;
  public Joystick turn;  
  public XboxController xbox;
  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    xbox = new XboxController(0);
    go = new Joystick(1);
    turn = new Joystick(2);
  beltsystem = new BeltSS(go);
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
    JoystickButton exampleButton = new JoystickButton(xbox, Xbox.BUTTON.RB.get());
  exampleButton.whileHeld(new elevatorC(m_exampleSubsystem,0.7));
  JoystickButton exampleButton2 = new JoystickButton(xbox, Xbox.BUTTON.LB.get());
  exampleButton2.whileHeld(new elevatorC(m_exampleSubsystem,-0.2));

  JoystickButton NBdriveteleop = new JoystickButton(xbox, Xbox.BUTTON.A.get());
  NBdriveteleop.toggleWhenPressed(new DriveTeleop(drivetrain,this,1));
  JoystickButton NBdriveteleopB = new JoystickButton(xbox, Xbox.BUTTON.B.get());
  NBdriveteleopB.toggleWhenPressed(new DriveTeleop(drivetrain,this,-1));
  JoystickButton ShootB = new JoystickButton(go, 1);
  ShootB.toggleWhenPressed(new ShooterC(shootersystem,-0.4));
  JoystickButton IntakeB = new JoystickButton(go,6 );
  IntakeB.whileHeld(new IntakeC(intakesystem,0.40));
  JoystickButton IntakeBA = new JoystickButton(go,4 );
  IntakeBA.whileHeld(new IntakeC(intakesystem,-0.40));

  JoystickButton WinchBA = new JoystickButton(xbox,Xbox.BUTTON.Y.get() );
  WinchBA.whileHeld(new WinchC(winchsystem,-0.8));

 
  JoystickButton ShootC = new JoystickButton(go, 10);
  ShootC.toggleWhenPressed(new ShooterC(shootersystem,-0.1));
JoystickButton PizzaCC = new JoystickButton(go, 8);
PizzaCC.whileHeld(new PizzaC(pizzasystem, 0.6));
JoystickButton PizzaCCA = new JoystickButton(go, 7);
PizzaCCA.whileHeld(new PizzaC(pizzasystem, -0.8));
}


  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    //first input is the drive train so it gets the right motors 2nd input is for speed third is for seconds until stop
    return new auto(drivetrain,-0.5,2);
  }
}
