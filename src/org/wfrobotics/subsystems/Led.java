package org.wfrobotics.subsystems;

import org.wfrobotics.commands.LED;
import org.wfrobotics.robot.RobotMap;

import com.mindsensors.*;

import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import javafx.scene.paint.Color;

public class Led extends Subsystem 
{
//    http://mindsensors.com/largefiles/CANLight/CANLight_Demo.java    
//    public static final Color Black = new Color(0, 0, 0);
//    public static final Color Red = new Color(0xfff, 0, 0);
//    public static final Color Yellow = new Color(0xfff, 0x8ff, 0);
//    public static final Color Green = new Color(0, 0xfff, 0);
//    public static final Color Cyan = new Color(0, 0xfff, 0xfff);
//    public static final Color Blue = new Color(0, 0, 0xfff);
//    public static final Color Magenta = new Color(0xfff, 0, 0xfff);
//    public static final Color White = new Color(0xfff, 0xfff, 0xfff);
//    public static final Color Orange = new Color(0xfff, 0x666, 0x000)
    public enum HARDWARE {TOP, SIDE, ALL};
       
   
    private CANLight topLights;
    private CANLight sideLights;
    
    public Led()
    {
        topLights = new CANLight(3);
        sideLights = new CANLight(4);
    }  
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new LED(HARDWARE.ALL, LED.MODE.OFF));
    }
    
   /*
    * blink(time, which strip)
    * setOn (bool, strip)
    * setOnColor (bool, strip, R, G, B,[or color if we go that way]) 
    * blinkColor(time, strip, R, G, B, [or color]    
    * INCOMPLETE 
    * ToDo: complete commands and finish this thing
    */
    public void setOn(HARDWARE hardware, boolean on)
    {
        if(hardware == HARDWARE.TOP)
        {
            topLights.showRGB(0, 0, 0xfff); //does only red 
        }
       
    }
    public void blink(HARDWARE hardware, int time)
    {
        if(hardware == HARDWARE.TOP)
        {
            topLights.blinkLED(time);
            topLights.showRGB(0, 0, 0xfff);
        }
   }
    public void setOnColor(HARDWARE hardware, boolean on, int r, int g, int b)
    {
        if(on)
        {
            
        
        if(hardware == HARDWARE.TOP)
        {
            topLights.showRGB(r, g, b);
        }
        if(hardware == HARDWARE.SIDE)
        {
            topLights.showRGB(r, g, b);
        }
        }
        else
        {
            if(hardware == HARDWARE.TOP)
            {
                topLights.showRGB(0, 0, 0);    
            }
            
        }
    }
}
