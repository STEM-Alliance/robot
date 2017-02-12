package org.wfrobotics.subsystems;

import org.wfrobotics.commands.LED;
import org.wfrobotics.robot.RobotMap;

import com.mindsensors.CANLight;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Led extends Subsystem 
{
    //    http://mindsensors.com/largefiles/CANLight/CANLight_Demo.java    
    //    public static final Color Black = new Color(0, 0, 0);
    //    public static final Color Red = new Color(0xfff, 0, 0);
    //    public static final Color Yellow = new Color(255, 103, 0);
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
        if(on)
        {
            if(hardware == HARDWARE.ALL || hardware == HARDWARE.TOP)
            {
                topLights.showRGB(0, 255, 0);
            }

            if(hardware == HARDWARE.ALL || hardware == HARDWARE.SIDE)
            {
                sideLights.showRGB(0, 255, 0);  
            }
        }
        else
        {            
            if(hardware == HARDWARE.ALL || hardware == HARDWARE.TOP)
            {
                topLights.showRGB(0, 0, 0);  
            }

            if(hardware == HARDWARE.ALL || hardware == HARDWARE.SIDE)
            {
                sideLights.showRGB(0, 0, 0); 
            }
        }
        SmartDashboard.putBoolean("LED is on", on);        
    }

    public void blink(HARDWARE hardware, double blinkLength)
    {
        if(hardware == HARDWARE.TOP)
        {

            topLights.writeRegister(0, blinkLength, 0, 255, 0);  
            topLights.writeRegister(1, blinkLength, 255, 103, 0);
            topLights.fade(0, 1);
        }

        if(hardware == HARDWARE.SIDE)
        {
            topLights.writeRegister(0, blinkLength, 0, 255, 0);  
            topLights.writeRegister(1, blinkLength, 255, 103, 0);            
            topLights.cycle(0, 1);

        }

        if(hardware == HARDWARE.ALL)
        {

            topLights.writeRegister(0, blinkLength, 0, 255, 0);  
            topLights.writeRegister(1, blinkLength, 255, 103, 0);            
            topLights.fade(0, 1);
        }
    }

    public void setOnColor(HARDWARE hardware, boolean on, int r, int g, int b)
    {
        if(on)
        {             
            if(hardware == HARDWARE.ALL || hardware == HARDWARE.TOP)
            {
                topLights.showRGB(r, g, b);
            }

            if(hardware == HARDWARE.ALL || hardware == HARDWARE.SIDE)
            {
                sideLights.showRGB(r, g, b);
            }
        }
        else
        {
            if(hardware == HARDWARE.ALL || hardware == HARDWARE.TOP)
            {
                topLights.showRGB(0, 0, 0);    
            }

            if(hardware == HARDWARE.ALL || hardware == HARDWARE.SIDE)
            {
                sideLights.showRGB(0, 0, 0);
            }            
        }
    }
}
