package org.wfrobotics.subsystems;

import org.wfrobotics.commands.LED;
import org.wfrobotics.robot.RobotMap;

import com.mindsensors.CANLight;

import edu.wpi.first.wpilibj.command.Subsystem;

//http://mindsensors.com/largefiles/CANLight/CANLight_Demo.java      
public class Led extends Subsystem 
{
    private class LedControler
    {
        private CANLight ledLights; 
        
        public LedControler(int CANid)
        {            
            ledLights = new CANLight(CANid);
        }
        
        public void writeReg(int index, double time, int r, int g, int b)
        {
            ledLights.writeRegister(index, time, r, g, b);
        }
        public void solid(int index, double time, int r, int g, int b)
        {
            ledLights.writeRegister(index, time, r, g, b);
            ledLights.showRegister(index);
        }
        public void fade(int idx1, int idx2)
        {
            ledLights.fade(idx1, idx2);
        }
        public void flashGreen()
        {  
            ledLights.writeRegister(6, .7, 0, 255, 0);
            ledLights.writeRegister(7, .7, 100, 103, 0);
            ledLights.cycle(6, 7);
        }
        public void fadeBtwnColors(double time, int index1, int index2, int r, int g, int b, int r2, int g2, int b2)
        { 
                ledLights.writeRegister(index1, time, r, g, b);
                ledLights.writeRegister(index2, time, r2, g2, b2);
          
                ledLights.fade(4, 5);          
        }
        public void off()
        {
            ledLights.showRGB(0, 0, 0);
        }
    }
   
    LedControler top;
        
        public Led()
        {
            top = new LedControler(RobotMap.CAN_LIGHT[0]);
            top.flashGreen();
        }  
        protected void initDefaultCommand()
        {
            setDefaultCommand(new LED(LED.MODE.OFF));
    
        }   
        public void blinkRed(double blinkLength)
        {   
            top.writeReg(4, blinkLength, 255, 0, 0);
            top.writeReg(5, blinkLength, 0, 0, 0);
            top.fade(4, 5);      
        }
        public void fadebtwColors(double time, int index1, int index2, int r,int g, int b, int r2, int g2, int b2)
        {
            top.fadeBtwnColors(time, index1, index2, r, g, b, r2, g2, b2);
        }
        public void solid(double time, int index, int r, int g, int b )
        {
            top.solid(index,time, r, g, b);
        }
        public void off()
        {
            top.off();
        }
}

