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
    Button panelRightYellow = new PanelButton(panel, Panel.ButtonType.kYellowR);
    Button panelRightGreen = new PanelButton(panel, Panel.ButtonType.kGreenR);
    Button panelRightBlack = new PanelButton(panel, Panel.ButtonType.kBlackR);
    
    public OI() 
    {
        buttonStart1.toggleWhenPressed(new DriveArcadeWithXbox());
        buttonBack1.toggleWhenPressed(new DriveTankWithXbox(true));
        
        buttonRB1.whileHeld(new ManipulatorContinous(true));
        buttonLB1.whileHeld(new ManipulatorContinous(false));
        
        buttonLeftTrigger1.whileHeld(new CameraChange(true));

        buttonA1.whileHeld(new KickerContinuous(true));
        buttonB1.whileHeld(new KickerContinuous(false));
//        buttonY1.whileHeld(new KickerToAngle(180));

//        buttonA1.whileHeld(new KickerContinuous(true));
//        buttonB1.whileHeld(new KickerContinuous(false));
        
        ////////////////////////
        
        buttonA2.whileHeld(new ShooterGrab());
        buttonB2.whileHeld(new ShooterFire());
        
        buttonLeftTrigger2.whileHeld(new ShooterRev());
        buttonRightTrigger2.whenPressed(new ShooterRelease());

        buttonLB2.whenPressed(new LiftToTop());
        buttonRB2.whenPressed(new LiftToBottom());
        buttonStart2.toggleWhenPressed(new LiftStop());
        
//        buttonX2.whileHeld(new AimerContinuous(true));
//        buttonY2.whileHeld(new AimerContinuous(false));

        buttonX2.whenPressed(new AimerToAngle(90));
        buttonY2.whenPressed(new AimerToAngle(125));

        buttonBack2.toggleWhenPressed(new AimerLEDs(true));
        buttonStart2.toggleWhenPressed(new AimerLEDs(false));
        
        //////////////////////////
        
        panelLeftWhite.whileHeld(new LiftCal());
        panelLeftBlack.whileHeld(new LoadBallFromFloor());
        panelRightWhite.whileHeld(new LoadBall());

        panelRightYellow.whileHeld(new LiftContinuous(true));
        panelRightGreen.whileHeld(new LiftContinuous(false));
        panelRightBlack.whileHeld(new ClimberClaw(true));
    }
    
    public static boolean getTractionControl()
    {
        return false;//xbox1.getBumper(Hand.kRight);
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
    
    public static double getShooterSpeedAdjust()
    {
        //return xbox2.getTriggerVal(Hand.kRight);
        return 1;
    }
    
    public static int getDpad2()
    {
        return xbox2.getPOV(0);
    }

    public static double getTractionMiddleIncrease()
    {
        return xbox1.getTriggerVal(Hand.kRight);
    }
    
    public static void setRumble1(RumbleType type, float value)
    {
        xbox1.setRumble(type, value);
    }

    
    public static void setRumble2(RumbleType type, float value)
    {
        xbox2.setRumble(type, value);
    }

    public static double getAimerY()
    {
        return xbox2.getY(Hand.kLeft);
    }
    
}

