package com.taurus.robotspecific2015;

import edu.wpi.first.wpilibj.Talon;

import java.util.ArrayList;

public class MotorSystem
{
   private ArrayList<Talon> Talons;
   private ArrayList<Double> Scaling;
   
   public MotorSystem(int[] pins)
   {
       Talons = new ArrayList<Talon>();
       Scaling = new ArrayList<Double>();
      for(int index = 0; index < pins.length; index++)
      {
         Talons.add(new Talon(pins[index]));
         Scaling.add((double) 1);
      }
   }
   
   // Set the Talons to a speed between -1 and 1
   public void Set(double speed)
   {
      for(int index = 0; index < Talons.size(); index++)
      {
         Talons.get(index).set(speed * Scaling.get(index));
      }
   }
   
   // Scales the input to Set()
   public void SetScale(double[] motorScaling)
   {
      for(int index = 0; index < Talons.size(); index++)
      {
         Scaling.set(index, motorScaling[index]);
      }
   }
}
