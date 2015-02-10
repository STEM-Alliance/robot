package com.taurus.robotspecific2015;



import com.taurus.robotspecific2015.Constants.CYLINDER_STATE;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;


// Wraps FIRST code to control a group of pneumatic cylinders
public class PneumaticSubsystem {
    private final int ChannelExtend;
    private final int ChannelContract;
    private final int Module;
    
    private final double TimeExtend;
    private final double TimeContract;

    private CYLINDER_STATE State = CYLINDER_STATE.NONE;
    
    private double TimeStart;

    private final DoubleSolenoid solenoid;

    /**
     * Initialize PCU (pneumatic control unit) for this pneumatic subsytem
     * @param channels array of 2 channels for double solenoid
     * @param module module id of PCU
     * @param timeExtend time it takes to extend
     * @param timeContract time it takes to contract
     * @param startExtended whether to start extended or contracted
     */
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

    /**
     * Extend all of the solenoids to the 'on' position
     * @return true if finished
     */
    public boolean Extend()
    {
        boolean done = false;

        if (State == CYLINDER_STATE.EXTENDED)
        {
            // Already extended
            done = true;
        }
        else if (State == CYLINDER_STATE.CONTRACTING || State == CYLINDER_STATE.CONTRACTED)
        {
            // If we are not moving, start extending
            solenoid.set(DoubleSolenoid.Value.kForward);

            // Update current state
            TimeStart = Timer.getFPGATimestamp();
            State = CYLINDER_STATE.EXTENDING;
        }
        else
        {
            // We are extending. Check if we are done extending.
            if (Timer.getFPGATimestamp() - TimeStart > TimeExtend)
            {
                State = CYLINDER_STATE.EXTENDED; // Record the position
                done = true;
            }
        }

        return done;
    }

    /**
     * Contract all of the solenoids to the 'off' position
     * @return true if finished
     */
    public boolean Contract()
    {
        boolean done = false;

        if (State == CYLINDER_STATE.CONTRACTED)
        {
            // Already contracted
            done = true;
        }
        else if (State == CYLINDER_STATE.EXTENDING || State == CYLINDER_STATE.EXTENDED)
        {
            // If we are not moving, start contracting
            solenoid.set(DoubleSolenoid.Value.kReverse);

            // Update current state
            TimeStart = Timer.getFPGATimestamp();
            State = CYLINDER_STATE.CONTRACTING;
        }
        else
        {
            // We are contracting. Check if we are done contracting.
            if (Timer.getFPGATimestamp() - TimeStart > TimeContract)
            {
                State = CYLINDER_STATE.CONTRACTED; // Record the position
                done = true;
            }
        }

        return done;
    }

    /**
     * Return current position of solenoids
     * @return true if it is extended, false if contracted
     */
    public boolean IsExtended()
    {
        return State == CYLINDER_STATE.EXTENDED || State == CYLINDER_STATE.CONTRACTING;
    }
}
