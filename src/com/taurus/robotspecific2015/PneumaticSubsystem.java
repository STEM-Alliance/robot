package com.taurus.robotspecific2015;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.hal.SolenoidJNI;

// Wraps FIRST code to control a group of pneumatic cylinders
public class PneumaticSubsystem {
    private int ChannelExtend;
    private int ChannelContract;
    private boolean Extended;
    private boolean Extending = false;
    private boolean Contracting = false;
    private double TimeExtend;
    private double TimeContract;
    private double TimeStart;
    private int Module;

    private DoubleSolenoid solenoid;

    // Constructor - Initialize PCU (pneumatic control unit) for this pneumatic
    // subsystem
    public PneumaticSubsystem(int[] channels,
            int module, double timeExtend, double timeContract,
            boolean startExtended)
    {
        // Save any constants
        Module = module;
        ChannelExtend = channels[0];
        ChannelContract = channels[1];
        TimeExtend = timeExtend;
        TimeContract = timeContract;
        solenoid = new DoubleSolenoid(Module, ChannelExtend, ChannelContract);

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

            solenoid.set(DoubleSolenoid.Value.kForward);

            // Update current state
            TimeStart = Timer.getFPGATimestamp();
            Extending = true;
        }
        else
        {
            // We are extending. Check if we are done extending.
            if (Timer.getFPGATimestamp() - TimeStart > TimeExtend)
            {
                Extending = false; // Record that cylinder is done moving
                Extended = true; // Record the position
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
            solenoid.set(DoubleSolenoid.Value.kReverse);

            // Update current state
            TimeStart = Timer.getFPGATimestamp();
            Contracting = true;
        }
        else
        {
            // We are contracting. Check if we are done contracting.
            if (Timer.getFPGATimestamp() - TimeStart > TimeContract)
            {
                Contracting = false; // Record that cylinder is done moving
                Extended = false; // Record the position
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
}
