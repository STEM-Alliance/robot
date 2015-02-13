package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

public class Ejector {
    private STATE_EJECT StateEject = STATE_EJECT.PUSHER_EXTEND;
    private POSITION_EJECTOR PositionEjector = POSITION_EJECTOR.IN;

    private MotorSystem Motors;
    private PneumaticSubsystem CylindersStop;
    private PneumaticSubsystem CylindersPusher;
    private Sensor OutSensor;
    private Sensor InSensor;

    public Ejector()
    {
        Motors = new MotorSystem(Constants.PINS_EJECTOR);
        CylindersStop = new PneumaticSubsystem(Constants.CHANNEL_STOP,
                Constants.PCU_STOP, Constants.TIME_EXTEND_STOP,
                Constants.TIME_CONTRACT_STOP, Constants.CYLINDER_ACTION.EXTEND);
        CylindersPusher = new PneumaticSubsystem(Constants.CHANNEL_PUSHER,
                Constants.PCU_PUSHER, Constants.TIME_EXTEND_PUSHER,
                Constants.TIME_CONTRACT_PUSHER, Constants.CYLINDER_ACTION.CONTRACT);
        OutSensor = new SensorDigital(Constants.CHANNEL_DIGITAL_EJECTOR_OUT);
        InSensor = new SensorDigital(Constants.CHANNEL_DIGITAL_EJECTOR_IN);

        Motors.SetScale(Constants.SCALING_EJECTOR);

        // Move the Ejector to its initial "in" position
        MoveIn();
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
                if (MoveOut())
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
            if (MoveIn() & CylindersPusher.Contract())
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

        if (CylindersStop.Extend() && MoveOut())
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

        if (CylindersStop.Contract() && MoveIn())
        {
            done = true;
        }
        return done;
    }

    /**
     * Move the Ejector outward with the motors
     * 
     * @return true when finished
     */
    private boolean MoveOut()
    {
        if (OutSensor.IsOn()
                || PositionEjector == Constants.POSITION_EJECTOR.OUT)
        {
            // Stop motor and advance state machine state
            Motors.Set(0);
            PositionEjector = Constants.POSITION_EJECTOR.OUT;
        }
        else
        {
            Motors.Set(Constants.MOTOR_DIRECTION_FORWARD);
        }

        // Is the Ejector in the out position or not?
        return PositionEjector == Constants.POSITION_EJECTOR.OUT;
    }

    /**
     * Move the Ejector inward with the motors
     * 
     * @return true when finished
     */
    private boolean MoveIn()
    {
        if (InSensor.IsOn() || PositionEjector == Constants.POSITION_EJECTOR.IN)
        {
            // Stop motor and advance state machine state
            Motors.Set(0);
            PositionEjector = Constants.POSITION_EJECTOR.IN;
        }
        else
        {
            Motors.Set(Constants.MOTOR_DIRECTION_BACKWARD);
        }

        // Is the Ejector in the in position or not?
        return PositionEjector == Constants.POSITION_EJECTOR.IN;
    }

    /**
     * Set the motors' speed
     * 
     * @param speed
     */
    public void SetMotors(double speed)
    {
        Motors.Set(speed);
    }
}