package com.taurus;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import edu.wpi.first.wpilibj.hal.SolenoidJNI;
 
public class Lift
{
    byte Module = 9;
    
    public Lift ()
    {
        // Creates variables to interact with solenoid
        ByteBuffer port = SolenoidJNI.getPortWithModule( Module, (byte) 0);
        IntBuffer status = IntBuffer.allocate(1);
        
        // Initializes all solenoids
        SolenoidJNI.initializeSolenoidPort(port, status);
    }
    
    public void OpenSolenoid()
    {
        ByteBuffer port = SolenoidJNI.getPortWithModule( Module, (byte) 0);
        IntBuffer status = IntBuffer.allocate(1);
        
        SolenoidJNI.setSolenoid(port, (byte) 1, status);
    }
    
    public void CloseSolenoid()
    {
        ByteBuffer port = SolenoidJNI.getPortWithModule( Module, (byte) 0);
        IntBuffer status = IntBuffer.allocate(1);
        
        SolenoidJNI.setSolenoid(port, (byte) 0, status);
    }
}


