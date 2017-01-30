package org.wfrobotics.subsystems;

import org.wfrobotics.commands.LED;
import org.wfrobotics.robot.RobotMap;

import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Led extends Subsystem 
{
    public enum HARDWARE {TOP, SIDE, ALL};
    
    private AnalogOutput top;
    private AnalogOutput side;

    public Led()
    {
        top = new AnalogOutput(RobotMap.LED_TOP_ANALOG);
        side = new AnalogOutput(RobotMap.LED_SIDE_ANALOG);
    }  
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new LED(HARDWARE.ALL, LED.MODE.OFF));
    }
    
    public void setOn(HARDWARE hardware, boolean on)
    {
        double voltage = (on) ? 5:0;
        
        if (hardware == HARDWARE.ALL || hardware == HARDWARE.TOP)
        {
            top.setVoltage(voltage);
        }   
        
        if (hardware == HARDWARE.ALL || hardware == HARDWARE.SIDE)
        {
            side.setVoltage(voltage);
        }
    }
}
