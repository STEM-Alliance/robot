package com.taurus.robotspecific2015;

public class Ejector {

    private PneumaticSubsystem CylindersStop;

    public Ejector()
    {
        CylindersStop = new PneumaticSubsystem(Constants.CHANNEL_STOP[Constants.ROBOT_VERSION],
                Constants.PCM_STOP, Constants.TIME_EXTEND_STOP,
                Constants.TIME_CONTRACT_STOP, Constants.CYLINDER_ACTION.EXTEND);
    }

    /**
     * Disengage the stop for stopping totes from the chute
     * 
     * @return true when finished
     */
    public boolean StopOut()
    {
        boolean done = false;

        if (CylindersStop.Extend()/* && MoveOut()*/)
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

        if (CylindersStop.Contract()/* && MoveIn()*/)
        {
            done = true;
        }
        return done;
    }
    
    public PneumaticSubsystem GetCylindersStop()
    {
        return CylindersStop;
    }
}