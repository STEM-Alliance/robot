package com.taurus.robotspecific2015;



import com.taurus.robotspecific2015.Constants.CYLINDER_STATE;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;


// Wraps FIRST code to control a group of pneumatic cylinders
public class PneumaticSubsystem {
    private final double TimeExtend;
    private final double TimeContract;

    private CYLINDER_STATE State;
    
    private double TimeStart;

    private final DoubleSolenoid solenoid;

    /**
     * Initialize PCU (pneumatic control unit) for this pneumatic subsytem
     * @param channels array of 2 channels for double solenoid
     * @param module module id of PCU
     * @param timeExtend time it takes to extend
     * @param timeContract time it takes to contract
     * @param startAction whether to start extended or contracted
     */
    public PneumaticSubsystem(int[] channels,
            int module, double timeExtend, double timeContract,
            Constants.CYLINDER_ACTION startAction)
    {
        // Save any constants
        int Module = module;
        int ChannelExtend = channels[0];
        int ChannelContract = channels[1];
        TimeExtend = timeExtend;
        TimeContract = timeContract;
        solenoid = new DoubleSolenoid(Module, ChannelExtend, ChannelContract);
        State = CYLINDER_STATE.NONE;
                
        // Move all solenoids to initial positions
        Run(startAction);
    }
    
    /**
     * Initialize PCU (pneumatic control unit) for this pneumatic subsytem
     * @param solenoid a double solenoid
     * @param timeExtend time it takes to extend
     * @param timeContract time it takes to contract
     * @param startAction whether to start extended or contracted
     */
    public PneumaticSubsystem(DoubleSolenoid solenoid, double timeExtend, double timeContract,
            Constants.CYLINDER_ACTION startAction)
    {
        // Save any constants
        TimeExtend = timeExtend;
        TimeContract = timeContract;
        this.solenoid = solenoid;
        State = CYLINDER_STATE.NONE;
                
        // Move all solenoids to initial positions
        Run(startAction);
    }
    
    /**
     * Perform an action, either extend/contract/nothing
     * @param Action extend/contract/no action
     * @return true if finished
     */
    public boolean Run(Constants.CYLINDER_ACTION Action)
    {
        if(Action == Constants.CYLINDER_ACTION.EXTEND)
        {
            return Extend();
        }
        else if(Action == Constants.CYLINDER_ACTION.CONTRACT)
        {
            return Contract();
        }
        else
        {
            return false;
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
        else if (State == CYLINDER_STATE.CONTRACTING ||
                 State == CYLINDER_STATE.CONTRACTED ||
                 State == CYLINDER_STATE.NONE)
        {
            // If we are not extended/extending
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
        else if (State == CYLINDER_STATE.EXTENDING ||
                 State == CYLINDER_STATE.EXTENDED ||
                 State == CYLINDER_STATE.NONE)
        {
            // If we are not contracted/contracting
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
     * @return true if it is extended/contracting, false if contracted/extending
     */
    public boolean IsExtended()
    {
        return State == CYLINDER_STATE.EXTENDED || State == CYLINDER_STATE.CONTRACTING;
    }
}
