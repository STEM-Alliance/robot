package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.POSITION_CAR;

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
    public POSITION_CAR GetPosition()
    {
        POSITION_CAR result = POSITION_CAR.MOVING;
        int position = Actuator.GetPosition();

        if (position == 3)
        {
            result = POSITION_CAR.STACK;
        }
        else if (position == 2)
        {
            result = POSITION_CAR.DESTACK;
        }
        else if (position == 1)
        {
            result = POSITION_CAR.CHUTE;
        }
        else if (position == 0)
        {
            result = POSITION_CAR.BOTTOM;
        }
        return result;
    }
    
    /**
     * Go to position
     * @param i position index
     * @return
     */
    public boolean SetPosition(int i)
    {
        Actuator.SetPosition(i);
        return Actuator.GetPosition() == i;
    }

    /**
     * Move car to where the new tote will be held in place by the stack holder
     * @return
     */
    public boolean GoToStack()
    {
        Actuator.SetPosition(3);
        return Actuator.GetPosition() == 3;
    }

    /**
     * Move car to position that pushes last tote high enough to make room to
     * disengage stack holder
     * @return
     */
    public boolean GoToDestack()
    {
        Actuator.SetPosition(2);
        return Actuator.GetPosition() == 2;
    }

    /**
     * Move car to position that can receive totes from chute
     * @return
     */
    public boolean GoToChute()
    {
        Actuator.SetPosition(1);
        return Actuator.GetPosition() == 1;
    }

    /**
     * Move car to bottom position
     * @return
     */
    public boolean GoToBottom()
    {
        Actuator.SetPosition(0);
        return Actuator.GetPosition() == 0;
    }
    
    /**
     * Get the current height of the car in inches
     * @return
     */
    public double GetHeight()
    {
        return Actuator.GetDistance();
    }

    public void ZeroIfNeeded()
    {
        if (ZeroSensor.IsOn())
        {
            Actuator.Zero();
        }
    }
}