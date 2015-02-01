package com.taurus.robotspecific2015;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.hal.SolenoidJNI;

// Wraps FIRST code to control a group of pneumatic cylinders
public class PneumaticSubsystem
{
   private int Channel;
   private boolean Extended;
   private boolean Extending = false;
   private boolean Contracting = false;
   private double TimeExtend;
   private double TimeContract;
   double TimeStart;

   SolenoidJNI solenoid;

   // Constructor - Initialize PCU (pneumatic control unit) for this pneumatic subsystem
   public PneumaticSubsystem(int channel, double timeExtend, double timeContract, boolean startExtended)
   {
      // Save any constants
      Channel = channel;
      TimeExtend = timeExtend;
      TimeContract = timeContract;

      ByteBuffer port = SolenoidJNI.getPortWithModule(Constants.MODULE_ID_PCU, (byte) Channel);
      IntBuffer status = IntBuffer.allocate(1);

      // Initializes all solenoids
      SolenoidJNI.initializeSolenoidPort(port, status);

      // Move all solenoids to initial positions
      if (startExtended)
      {
         Extend();
      }
      else
      {
         Contract();
      }
   }

   // Extend all of the solenoids to the 'on' position
   public boolean Extend()
   {
      boolean done = false;      
      
      if (Extended)
      {
         // Already extended
         done = true;
      }
      else if (Extending == false && Contracting == false)
      {
         // If we are not moving, start extending
         ByteBuffer port = SolenoidJNI.getPortWithModule(Constants.MODULE_ID_PCU, (byte) Channel);
         IntBuffer status = IntBuffer.allocate(1);
   
         // Extend solenoids
         SolenoidJNI.setSolenoid(port, (byte) 1, status);
   
         // Update current state
         TimeStart = Timer.getFPGATimestamp();
         Extending = true;
      }
      else
      {       
         // We are extending. Check if we are done extending.
         if (Timer.getFPGATimestamp() - TimeStart > TimeExtend)
         {
            Extending = false;  // Record that cylinder is done moving
            Extended = true;  // Record the position
            done = true;
         }
      }
      
      return done;
   }

   // Contract all of the solenoids to the 'off' position
   public boolean Contract()
   {
      boolean done = false;      
      
      if (!Extended)
      {
         // Already contracted
         done = true;
      }
      else if (Extending == false && Contracting == false)
      {
         // If we are not moving, start contracting
         ByteBuffer port = SolenoidJNI.getPortWithModule(Constants.MODULE_ID_PCU, (byte) Channel);
         IntBuffer status = IntBuffer.allocate(1);
   
         // Contract solenoids
         SolenoidJNI.setSolenoid(port, (byte) 0, status);
   
         // Update current state
         TimeStart = Timer.getFPGATimestamp();
         Contracting = true;
      }
      else
      {       
         // We are contracting. Check if we are done contracting.
         if (Timer.getFPGATimestamp() - TimeStart > TimeContract)
         {
            Contracting = false;  // Record that cylinder is done moving
            Extended = false;  // Record the position
            done = true;
         }
      }
      
      return done;
   }
  
   // Return current position of solenoids
   public boolean IsExtended()
   {
      return Extended;
   }
   
   private boolean IsDone()
   {
      double waitTime;
      boolean done = false;
      
      if (Extending)
      {
         waitTime = TimeExtend;
      }
      else if (Contracting)
      {
         waitTime = TimeContract;
      }
      else
      {
         waitTime = 0;  // If we call this method more than once, this will force a done response
      }      
      
      if (Timer.getFPGATimestamp() - TimeStart > waitTime)
      {
         done = true;
         
         // Record the position
         if (Extending)
         {
            Extended = true;
         }
         else if(Contracting)
         {
            Extended = false;
         }
         
         // Record that cylinder is done moving
         Extending = false;
         Contracting = false;
      }
      
      return done;
   }
}
