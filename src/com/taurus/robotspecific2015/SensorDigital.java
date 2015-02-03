package com.taurus.robotspecific2015;

import edu.wpi.first.wpilibj.DigitalInput;

public class SensorDigital extends Sensor
{
   DigitalInput Digital;  // FRC class 
   
   // Constructor
   public SensorDigital(int channel)
   {
       Digital = new DigitalInput(channel);
   }
    
   // Is the sensor "triggered" or in the "on" position?
   public boolean IsOn()
   {
      return Digital.get();  // Return if digital input pin is on
   }
}
