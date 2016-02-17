package com.taurus.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

import com.taurus.controller.Xbox;
import com.taurus.controller.XboxButton;
import com.taurus.commands.*;
import com.taurus.commands.ShooterRelease;
import com.taurus.commands.ShooterRev;

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
    Button buttonStart = new XboxButton(xbox, Xbox.ButtonType.kStart);
    
    public OI() {
        buttonA.whileHeld(new ShooterGrab());
        //buttonB.whenPressed(new ShooterRev());

        buttonRB.whenPressed(new LiftToTop());
        buttonLB.whenPressed(new LiftToBottom());
        
        buttonY.whileHeld(new LiftRaiseContinuous());
        buttonX.whileHeld(new LiftLowerContinuous());
        
        buttonY.whenPressed(new ShooterFire());

        buttonBack.toggleWhenPressed(new DriveArcadeWithXbox());
        buttonStart.toggleWhenPressed(new LiftStop());
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

