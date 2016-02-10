package com.taurus.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

import com.taurus.controller.Xbox;
import com.taurus.controller.XboxButton;
import com.taurus.commands.DriveArcadeWithXbox;
import com.taurus.commands.LiftToBottom;
import com.taurus.commands.LiftLowerContinuous;
import com.taurus.commands.LiftToTop;
import com.taurus.commands.LiftRaiseContinuous;
import com.taurus.commands.ShooterFire;
import com.taurus.commands.ShooterGrab;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    static Xbox xbox = new Xbox(0);
    
    Button buttonA = new XboxButton(xbox, Xbox.ButtonType.kA);
    Button buttonB = new XboxButton(xbox, Xbox.ButtonType.kB);

    Button buttonRB = new XboxButton(xbox, Xbox.ButtonType.kRB);
    Button buttonLB = new XboxButton(xbox, Xbox.ButtonType.kLB);
    
    Button buttonY = new XboxButton(xbox, Xbox.ButtonType.kY);
    Button buttonX = new XboxButton(xbox, Xbox.ButtonType.kX);
    
    Button buttonBack = new XboxButton(xbox, Xbox.ButtonType.kBack);
    
    public OI() {
        buttonA.whileHeld(new ShooterGrab());
        buttonB.whileHeld(new ShooterFire());

        buttonRB.whileHeld(new LiftToTop());
        buttonLB.whileHeld(new LiftToBottom());
        
        buttonY.whileHeld(new LiftRaiseContinuous());
        buttonX.whileHeld(new LiftLowerContinuous());
        
        buttonBack.toggleWhenPressed(new DriveArcadeWithXbox());
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
    public static double getThrottle()
    {
      return xbox.getY(Hand.kLeft);
    }
    public static double getTurn()
    {
        return xbox.getX(Hand.kLeft);
    }
}

