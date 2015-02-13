package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.LIFT_POSITIONS_E;

// State machine for lift
public class Car {

    private LinearActuator Actuator;
    private SensorDigital ZeroSensor;

    public Car()
    {
        Actuator = new LinearActuatorPot(Constants.LIFT_MOTOR_PINS,
                Constants.LIFT_MOTOR_SCALING,
                Constants.LIFT_POSTITIONS,
                Constants.LIFT_THRESHOLD,
                Constants.LIFT_POT_PIN,
                Constants.LIFT_POT_DISTANCE);
        
        ZeroSensor = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_ZERO);

        // Move the motor until you hit a sensor, then zero the encoder position
        if (ZeroSensor.IsOn())
        {
            Actuator.Zero();
        }
        else
        {
            //Actuator.SetSpeedRaw(Constants.MOTOR_DIRECTION_BACKWARD);
        }
    }

    public LinearActuator GetActuator()
    {
        return Actuator;
    }
    
    /**
     * Get the position of the car
     * @return
     */
    public LIFT_POSITIONS_E GetPosition()
    {
        return LIFT_POSITIONS_E.fromInt(Actuator.GetPosition());
    }
    
    /**
     * Go to position
     * @param position position enum val
     * @return
     */
    public boolean SetPosition(LIFT_POSITIONS_E position)
    {
        Actuator.SetPosition(position.ordinal());
        return Actuator.GetPosition() == position.ordinal();
    }

    /**
     * Move car to where the new tote will be held in place by the stack holder
     * @return
     */
    public boolean GoToStack()
    {
        return SetPosition(LIFT_POSITIONS_E.STACK);
    }

    /**
     * Move car to position that pushes last tote high enough to make room to
     * disengage stack holder
     * @return
     */
    public boolean GoToDestack()
    {
        return SetPosition(LIFT_POSITIONS_E.DESTACK);
    }

    /**
     * Move car to position that can receive totes from chute
     * @return
     */
    public boolean GoToChute()
    {
        return SetPosition(LIFT_POSITIONS_E.CHUTE);
    }

    /**
     * Move car to bottom position
     * @return
     */
    public boolean GoToBottom()
    {
        return ZeroIfNeeded();
    }
    
    /**
     * Get the current height of the car in inches
     * @return
     */
    public double GetHeight()
    {
        return Actuator.GetDistance();
    }

    public void GoToTop()
    {
        Actuator.SetSpeedRaw(Constants.MOTOR_DIRECTION_FORWARD);
    }

    public void GoToZero()
    {
        if(ZeroIfNeeded())
        {
            Actuator.SetSpeedRaw(0);
        }
        else
        {
            Actuator.SetSpeedRaw(Constants.MOTOR_DIRECTION_BACKWARD);
        }
    }
    
    public boolean ZeroIfNeeded()
    {
        if (ZeroSensor.IsOn())
        {
            Actuator.Zero();
            return true;
        }
        return false;
    }

    public Sensor GetZeroSensor()
    {
        return ZeroSensor;
    }

    public void UpdateLastPosition()
    {
        Actuator.UpdateLastPosition();
    }
}