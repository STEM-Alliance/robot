package com.taurus.robotspecific2015;

import com.taurus.DoubleSolenoidPulser;
import com.taurus.robotspecific2015.Constants.*;

public class Ejector {
    private STATE_EJECT StateEject = STATE_EJECT.PUSHER_EXTEND;

    private PneumaticSubsystem CylindersStop;
    private PneumaticSubsystem CylindersPusher;
    private DoubleSolenoidPulser CylindersPusherPulser;

    public Ejector()
    {
        CylindersStop = new PneumaticSubsystem(Constants.CHANNEL_STOP,
                Constants.PCU_STOP, Constants.TIME_EXTEND_STOP,
                Constants.TIME_CONTRACT_STOP, Constants.CYLINDER_ACTION.EXTEND);
        
        CylindersPusherPulser = new DoubleSolenoidPulser(
                Constants.PCU_PUSHER, 
                Constants.CHANNEL_PUSHER[0],
                Constants.CHANNEL_PUSHER[1],
                Application.prefs.getDouble("PusherForward", Constants.PUSHER_FORWARD_SPEED),
                Application.prefs.getDouble("PusherReverse", Constants.PUSHER_REVERSE_SPEED));
        
        CylindersPusher = new PneumaticSubsystem(
                CylindersPusherPulser,
                Constants.TIME_EXTEND_PUSHER,
                Constants.TIME_CONTRACT_PUSHER,
                Constants.CYLINDER_ACTION.EXTEND);
    }

    /**
     * Eject the stack
     * 
     * @return true when finished
     */
    public boolean EjectStack()
    {
        switch (StateEject)
        {
            case PUSHER_EXTEND:
                if (CylindersPusher.Extend())
                {
                    StateEject = STATE_EJECT.MOVE_OUT;
                }
                break;
            case MOVE_OUT:
            // if (MoveOut())
            {
                StateEject = STATE_EJECT.RESET;
            }
                break;
            // IMPORTANT: Resetting the Ejector needs to happen, but with a
            // seperate method call
            // This allows robot to asynchronous drive and reset the Ejector
            default:
                // TODO: Put error condition here
                break;
        }

        // Did we just finish ejecting the stack?
        return StateEject == STATE_EJECT.RESET;
    }

    /**
     * Asynchronously finish EjectStack(), which can be called while driving
     * 
     * @return true when finished
     */
    public boolean ResetEjectStack()
    {
        boolean finishedReset = false;

        if (StateEject == STATE_EJECT.RESET)
        {
            // IMPORTANT: Use single '&' to execute all cleanup routines
            // asynchronously
            if (/* MoveIn() & */CylindersPusher.Contract())
            {
                finishedReset = true;
                StateEject = STATE_EJECT.PUSHER_EXTEND;
            }
        }
        return finishedReset;
    }

    /**
     * Disengage the stop for stopping totes from the chute
     * 
     * @return true when finished
     */
    public boolean StopOut()
    {
        boolean done = false;

        if (CylindersStop.Extend()/* && MoveOut() */)
        {
            done = true;
        }
        return done;
    }

    /**
     * Engage the stop for stopping totes from the chute
     * 
     * @return true when finished
     */
    public boolean StopIn()
    {
        boolean done = false;

        if (CylindersStop.Contract()/* && MoveIn() */)
        {
            done = true;
        }
        return done;
    }

    public PneumaticSubsystem GetCylindersStop()
    {
        return CylindersStop;
    }

    public PneumaticSubsystem GetCylindersPusher()
    {
        return CylindersPusher;
    }
    
    public DoubleSolenoidPulser getCylindersPusherPulser()
    {
        return CylindersPusherPulser;
    }
    
}