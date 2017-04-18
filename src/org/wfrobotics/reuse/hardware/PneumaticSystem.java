package org.wfrobotics.reuse.hardware;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

// Wraps FIRST code to control a group of pneumatic cylinders
public class PneumaticSystem {
    private final int ChannelExtend;
    private final int ChannelContract;
    private final int Module;
    
    private final double TimeExtend;
    private final double TimeContract;

    private CYLINDER_STATE State;
    
    private double TimeStart;

    private final DoubleSolenoid solenoid;
    
    public static enum CYLINDER_ACTION {
        NONE, EXTEND, CONTRACT
    }
    
    public static enum CYLINDER_STATE {
        NONE, EXTENDED, EXTENDING, CONTRACTED, CONTRACTING
    }

    /**
     * Initialize PCU (pneumatic control unit) for this pneumatic subsytem
     * @param channels array of 2 channels for double solenoid
     * @param module module id of PCU
     * @param timeExtend time it takes to extend
     * @param timeContract time it takes to contract
     * @param startAction whether to start extended or contracted
     */
    public PneumaticSystem(int[] channels,
            int module, double timeExtend, double timeContract,
            CYLINDER_ACTION startAction)
    {
        // Save any constants
        Module = module;
        ChannelExtend = channels[0];
        ChannelContract = channels[1];
        TimeExtend = timeExtend;
        TimeContract = timeContract;
        solenoid = new DoubleSolenoid(Module, ChannelExtend, ChannelContract);
        State = CYLINDER_STATE.NONE;
                
        // Move all solenoids to initial positions
        Run(startAction);
    }
    
    /**
     * Perform an action, either extend/contract/nothing
     * @param Action extend/contract/no action
     * @return true if finished
     */
    public boolean Run(CYLINDER_ACTION Action)
    {
        if (Action == CYLINDER_ACTION.EXTEND)
        {
            return Extend();
        }
        else if (Action == CYLINDER_ACTION.CONTRACT)
        {
            return Contract();
        }
        else
        {
            return true;
        }
    }

    /**
     * Extend all of the solenoids to the 'on' position
     * @return true if finished
     */
    public boolean Extend()
    {
        solenoid.set(DoubleSolenoid.Value.kForward);

        switch (State)
        {
            case CONTRACTED:
            case CONTRACTING:
            case NONE:
                TimeStart = Timer.getFPGATimestamp();
                State = CYLINDER_STATE.EXTENDING;
                break;
                
            case EXTENDING:                
                if (Timer.getFPGATimestamp() > TimeStart + TimeExtend)
                {
                    State = CYLINDER_STATE.EXTENDED;
                }
                break;
                
            case EXTENDED:
                break;
        }

        return State == CYLINDER_STATE.EXTENDED;
    }

    /**
     * Contract all of the solenoids to the 'off' position
     * @return true if finished
     */
    public boolean Contract()
    {
        solenoid.set(DoubleSolenoid.Value.kReverse);

        switch (State)
        {
            case EXTENDED:
            case EXTENDING:
            case NONE:
                TimeStart = Timer.getFPGATimestamp();
                State = CYLINDER_STATE.CONTRACTING;
                break;
                
            case CONTRACTING:                
                if (Timer.getFPGATimestamp() > TimeStart + TimeContract)
                {
                    State = CYLINDER_STATE.CONTRACTED;
                }
                break;
                
            case CONTRACTED:
                break;
        }

        return State == CYLINDER_STATE.CONTRACTED;
    }

    /**
     * Return current position of solenoids
     * @return true if it is extended/contracting, false if contracted/extending
     */
    public boolean IsExtended()
    {
        switch (GetState())
        {
            case EXTENDED:
            case CONTRACTING:
                return true;
                
            default:
                return false;
        }
    }
    
    public CYLINDER_STATE GetState()
    {
        switch (State)
        {
            case EXTENDING:
                if (Timer.getFPGATimestamp() > TimeStart + TimeExtend)
                {
                    State = CYLINDER_STATE.EXTENDED;
                }
                break;
                
            case CONTRACTING:                
                if (Timer.getFPGATimestamp() > TimeStart + TimeContract)
                {
                    State = CYLINDER_STATE.CONTRACTED;
                }
                break;
                
            default:
                break;
        }
        
        return State;
    }
}
