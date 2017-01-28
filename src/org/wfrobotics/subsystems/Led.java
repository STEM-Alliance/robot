package org.wfrobotics.subsystems;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.wfrobotics.led.Color;
import org.wfrobotics.led.Effect;
import org.wfrobotics.led.Hardware;
import org.wfrobotics.robot.RobotMap;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Led extends Subsystem {

    // THIS NEEDS TO CHANGE
    public enum MODE { TOP, SIDE};
    private MODE mode;
//    private DigitalInput 
    
    //could be a digital ouput
    private AnalogOutput ledTop;
    private AnalogOutput ledSide;


    public Led()
    {
        ledTop = new AnalogOutput(RobotMap.LED_TOP[0]);
        ledSide = new AnalogOutput(RobotMap.LED_SIDE[1]);
    }  
    
    @Override
    protected void initDefaultCommand()
    {
        
    }
    
    public void setOn(boolean on, MODE mode)
    {
            if (mode == MODE.TOP)
            {
                if (on == true)
                {
                    // assuming that the LEDs are 5v
                    ledTop.setVoltage(5);
                }
                else
                {
                    ledTop.setVoltage(0);
                }
            }   
            else if (mode == MODE.SIDE)
            {
                if (on == true)
                {
                    // assuming that the LEDs are 5v
                    ledTop.setVoltage(5);
                }
                else
                {
                    ledTop.setVoltage(0);
                }
            }     
    }

}
