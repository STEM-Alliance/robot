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

    private CANLight top;
    private CANLight side;

    public Led()
    {
        top = new CANLight(RobotMap.CAN_LIGHT[0]);
        side = new CANLight(RobotMap.CAN_LIGHT[1]);
        
        flashGreen(HARDWARE.ALL);
    }  

    @Override
    protected void initDefaultCommand()
    {
//        setDefaultCommand(new LED(HARDWARE.ALL, LED.MODE.OFF));
    }
    
    public void flashGreen(HARDWARE hardware)
    {
//        if(hardware == HARDWARE.ALL || hardware == HARDWARE.TOP)
//        {
//            top.writeRegister(6, .7, 0, 255, 0);
//            top.writeRegister(7, .7, 100, 103, 0);
//            top.cycle(6, 7);
//        }
//        if(hardware == HARDWARE.ALL || hardware == HARDWARE.SIDE)
//        {
//            side.writeRegister(6, .7, 0, 255, 0);
//            side.writeRegister(7, .7, 100, 103, 0);
//            side.cycle(6, 7);
//        }
    }
    
    /*
     * blink(time, which strip)
     * setOn (bool, strip)
     * setOnColor (bool, strip, R, G, B,[or color if we go that way]) 
     * blinkColor(time, strip, R, G, B, [or color]     
     */
    public void setOn(HARDWARE hardware, boolean on)
    {
//        int r = 0;
//        int g = (on) ? 255:0;
//        int b = 0;
//
//        if(hardware == HARDWARE.ALL || hardware == HARDWARE.TOP)
//        {
//            top.showRGB(r, g, b);
//        }
//
//        if(hardware == HARDWARE.ALL || hardware == HARDWARE.SIDE)
//        {
//            side.showRGB(r, g, b);  
//        }
//        SmartDashboard.putBoolean("LED is on", on);        
    }

    //@Deprecated
    public void blinkRed(HARDWARE hardware, double blinkLength)
    {
//        if(hardware == HARDWARE.ALL || hardware == HARDWARE.TOP)
//        {
//            top.writeRegister(0, blinkLength, 255, 0, 0);  
//            top.writeRegister(1, blinkLength, 0, 0, 0);
//            top.cycle(0, 1);
//        }
//
//        if(hardware == HARDWARE.ALL || hardware == HARDWARE.SIDE)
//        {       
//            side.writeRegister(0, blinkLength, 255, 0, 0);  
//            side.writeRegister(1, blinkLength, 0, 0, 0);
//            side.cycle(0, 1);
//        }
    }

    public void setOnColor(HARDWARE hardware, boolean on, int r, int g, int b)
    {
//        if (!on)
//        {
//            r = 0;
//            g = 0;
//            b = 0;
//        }
//
//        if(hardware == HARDWARE.ALL || hardware == HARDWARE.TOP)
//        {
//            top.showRGB(r, g, b);
//        }
//
//        if(hardware == HARDWARE.ALL || hardware == HARDWARE.SIDE)
//        {
//            side.showRGB(r, g, b);
//        }
    }
}
