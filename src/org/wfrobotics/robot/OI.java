package org.wfrobotics.robot;

import org.wfrobotics.commands.LED;
import org.wfrobotics.commands.LED.MODE;
import org.wfrobotics.controller.Panel;
import org.wfrobotics.controller.Panel.COLOR;
import org.wfrobotics.controller.Xbox;
import org.wfrobotics.controller.XboxButton;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI 
{
    private final static double CLIMB_DEADBAND = .2;
    
    static Xbox xboxDrive = new Xbox(0);
    static Joystick joystickDrive = new Joystick(0);
    static Xbox xboxMan = new Xbox(1);
    public static Panel panel = new Panel(2);
    
    
    Button a = new XboxButton ( xboxDrive, Xbox.BUTTON.A);
    Button b = new XboxButton (xboxDrive, Xbox.BUTTON.B);
    Button x = new XboxButton (xboxDrive, Xbox.BUTTON.X);
     Button y = new XboxButton (xboxDrive, Xbox.BUTTON.Y);

    
    public OI()
    {        
        
      a.toggleWhenPressed(new LED(MODE.BLINK));
      b.toggleWhenPressed(new LED(MODE.SOLID));
      
//      x.whenPressed(new );
//      y.whenPressed(new );

          
    }
    


    public static double getAugerSpeedAdjust()
    {   
        return xboxMan.getTriggerAxis(Hand.kLeft);
    }
    
    public static void setDriveRumble(XboxController.RumbleType type, float value)
    {
        xboxDrive.setRumble(type, value);
    }

    public static void setPanelLEDs(Hand hand, COLOR LED1, COLOR LED2, COLOR LED3, COLOR LED4)
    {
        panel.setLEDs(hand, LED1, LED2, LED3, LED4);
    }
    
    public static void setPanelLEDs(COLOR ledsL[], COLOR ledsR[])
    {
        panel.setLEDs(Hand.kLeft, ledsL[0], ledsL[1], ledsL[2], ledsL[3]);
        panel.setLEDs(Hand.kRight, ledsR[0], ledsR[1], ledsR[2], ledsR[3]);
    }
}

