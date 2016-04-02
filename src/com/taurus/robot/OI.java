package com.taurus.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

import com.taurus.controller.Panel;
import com.taurus.controller.PanelButton;
import com.taurus.controller.Xbox;
import com.taurus.controller.Xbox.RumbleType;
import com.taurus.controller.XboxButton;
import com.taurus.controller.XboxTriggerButton;
import com.taurus.commands.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI 
{
    static Xbox xbox1 = new Xbox(0);
    static Xbox xbox2 = new Xbox(1);
    static Panel panel = new Panel(2);
    
    //first controller
    Button buttonA1 = new XboxButton(xbox1, Xbox.ButtonType.kA);
    Button buttonB1 = new XboxButton(xbox1, Xbox.ButtonType.kB);
    
    Button buttonY1 = new XboxButton(xbox1, Xbox.ButtonType.kY);
    Button buttonX1 = new XboxButton(xbox1, Xbox.ButtonType.kX);
    
    Button buttonLeftTrigger1 = new XboxTriggerButton(xbox1, Xbox.Hand.kLeft);

    Button buttonRB1 = new XboxButton(xbox1, Xbox.ButtonType.kRB);
    Button buttonLB1 = new XboxButton(xbox1, Xbox.ButtonType.kLB);

    Button buttonBack1 = new XboxButton(xbox1, Xbox.ButtonType.kBack);
    Button buttonStart1 = new XboxButton(xbox1, Xbox.ButtonType.kStart);
    
    //second controller
    Button buttonA2 = new XboxButton(xbox2, Xbox.ButtonType.kA);
    Button buttonB2 = new XboxButton(xbox2, Xbox.ButtonType.kB);
    
    Button buttonY2 = new XboxButton(xbox2, Xbox.ButtonType.kY);
    Button buttonX2 = new XboxButton(xbox2, Xbox.ButtonType.kX);

    Button buttonRB2 = new XboxButton(xbox2, Xbox.ButtonType.kRB);
    Button buttonLB2 = new XboxButton(xbox2, Xbox.ButtonType.kLB);

    Button buttonBack2 = new XboxButton(xbox2, Xbox.ButtonType.kBack);
    Button buttonStart2 = new XboxButton(xbox2, Xbox.ButtonType.kStart);

    Button buttonRightTrigger2 = new XboxTriggerButton(xbox2, Xbox.Hand.kRight);
    Button buttonLeftTrigger2 = new XboxTriggerButton(xbox2, Xbox.Hand.kLeft);

    
    // panel
    Button panelLeftWhite = new PanelButton(panel, Panel.ButtonType.kWhiteL);
    Button panelRightWhite = new PanelButton(panel, Panel.ButtonType.kWhiteR);
    Button panelLeftBlack = new PanelButton(panel, Panel.ButtonType.kBlackL);
    Button panelLeftYellow = new PanelButton(panel, Panel.ButtonType.kYellowL);
    Button panelRightYellow = new PanelButton(panel, Panel.ButtonType.kYellowR);
    Button panelRightGreen = new PanelButton(panel, Panel.ButtonType.kGreenR);
    Button panelRightBlack = new PanelButton(panel, Panel.ButtonType.kBlackR);
    
    public OI() 
    {
        buttonStart1.toggleWhenPressed(new DriveArcadeWithXbox());
        buttonBack1.toggleWhenPressed(new DriveTankWithXbox(true));
                
        //////////////////////////
        
        //////////////////////////
    }
    
    public static double getSpeedLeft()
    {
        return xbox1.getY(Hand.kLeft);
    }
    
    public static double getSpeedRight()
    {
        return xbox1.getY(Hand.kRight);        
    }
    
    public static double getThrottleY()
    {
      return xbox1.getY(Hand.kLeft);
    }
    
    public static double getThrottleX()
    {
        return xbox1.getX(Hand.kLeft);
    }
     
    public static double getThrottleHighSpeed()
    {
        return xbox1.getTriggerVal(Hand.kLeft);
    }
    
    public static int getDpad1()
    {
        return xbox1.getPOV(0);
    }

    public static int getDpad2()
    {
        return xbox2.getPOV(0);
    }
    
    public static void setRumble1(RumbleType type, float value)
    {
        xbox1.setRumble(type, value);
    }

    public static void setRumble2(RumbleType type, float value)
    {
        xbox2.setRumble(type, value);
    }
}

