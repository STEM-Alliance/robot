package com.taurus.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

import com.taurus.controller.Xbox;
import com.taurus.controller.XboxButton;
import com.taurus.commands.ShooterFire;
//import com.taurus.commands.ExampleCommand;
import com.taurus.commands.ShooterGrab;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    // Joystick stick = new Joystick(port);
    // Button button = new JoystickButton(stick, buttonNumber);
    static Xbox xbox = new Xbox(0);
    Button buttonA = new XboxButton(xbox, Xbox.ButtonType.kA);
    Button buttonB = new XboxButton(xbox, Xbox.ButtonType.kB);
    
    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.
    
    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:
    
    // Start the command when the button is pressed and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenPressed(new ExampleCommand());
    
    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());
    
    // Start the command when the button is released  and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenReleased(new ExampleCommand());
    
    public OI() {
        buttonA.whileHeld(new ShooterGrab());
        buttonB.whileHeld(new ShooterFire());
    }
    
    public static double getSpeedLeft()
    {
        return xbox.getY(Hand.kLeft);
    }
    
    public static double getSpeedRight()
    {
        return xbox.getY(Hand.kRight);        
    }
    
    public static double getTriggerRight()
    {
        return xbox.getTriggerVal(Hand.kRight);
    }
}

