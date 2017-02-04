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
        topLights = new CANLight(RobotMap.CAN_LIGHT[0]);
        sideLights = new CANLight(RobotMap.CAN_LIGHT[1]);
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
    */
    public void setOn(HARDWARE hardware, boolean on)
    {
        if(hardware == HARDWARE.TOP)
        {
            topLights.showRGB(0, 0, 0xfff);  
        }
        if(hardware == HARDWARE.SIDE)
        {
            sideLights.showRGB(0, 0, 0xfff); 
        }
        if(hardware == HARDWARE.ALL)
        {
            topLights.showRGB(0, 0, 0xfff); 
            sideLights.showRGB(0, 0, 0xfff); 
        }
    }
    public void blink(HARDWARE hardware, int time)
    {
        if(hardware == HARDWARE.TOP)
        {
            topLights.blinkLED(time);
            topLights.showRGB(0, 0, 0xfff);
        }
        if(hardware == HARDWARE.SIDE)
        {
            sideLights.blinkLED(time);
            topLights.showRGB(0, 0, 0xfff);
        }
        if(hardware == HARDWARE.ALL)
        {
            sideLights.blinkLED(time);
            topLights.blinkLED(time);

            topLights.showRGB(0, 0, 0xfff);
            sideLights.showRGB(0, 0, 0xfff);

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
            sideLights.showRGB(r, g, b);
        }
        if(hardware == HARDWARE.ALL)
        {
            topLights.showRGB(r, g, b);
            sideLights.showRGB(r, g, b);
        }
        
        }
        else
        {
            if(hardware == HARDWARE.TOP)
            {
                topLights.showRGB(0, 0, 0);    
            }
            if(hardware == HARDWARE.SIDE)
            {
                sideLights.showRGB(0, 0, 0);
            }
            if(hardware == HARDWARE.ALL)
            {
                topLights.showRGB(0, 0, 0);
                sideLights.showRGB(0, 0, 0);
            }
            
        }
    }
}
