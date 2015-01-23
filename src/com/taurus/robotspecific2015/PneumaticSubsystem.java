package com.taurus.robotspecific2015;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import edu.wpi.first.wpilibj.hal.SolenoidJNI;

// Wraps FIRST code to control a group of pneumatic cylinders
public class PneumaticSubsystem 
{
	private int Channel;
	private boolean Extended;
	
	SolenoidJNI solenoid;
	
	// Constructor - Initialize PCU (pneumatic control unit) for this pneumatic subsystem
	public PneumaticSubsystem(int channel, boolean startExtended)
	{
		Channel = channel;
		
        ByteBuffer port = SolenoidJNI.getPortWithModule(Constants.PCUModuleID, (byte) Channel);
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
	public void Extend()
	{
        ByteBuffer port = SolenoidJNI.getPortWithModule(Constants.PCUModuleID, (byte) Channel);
        IntBuffer status = IntBuffer.allocate(1);
        
        // Extend solenoids
		SolenoidJNI.setSolenoid(port, (byte) 1, status);
        
        // Update current state
	    Extended = true;
	}
	
	// Contract all of the solenoids to the 'off' position
	public void Contract()
	{
        ByteBuffer port = SolenoidJNI.getPortWithModule(Constants.PCUModuleID, (byte) Channel);
        IntBuffer status = IntBuffer.allocate(1);
        
        // Contract solenoids
        SolenoidJNI.setSolenoid(port, (byte) 0, status);
		
        // Update current state
		Extended = false;
	}
	
	// Return current position of solenoids
	public boolean IsExtended()
	{
		return Extended;
	}
}
