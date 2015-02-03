package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.POSITION_CAR;

// State machine for lift
public class Car {
    private POSITION_CAR CurrentState;

    MotorSystem Motors;
    Sensor SensorStack;
    Sensor SensorDestack;
    Sensor SensorChute;
    Sensor SensorBottom;

    public Car()
    {
        Motors = new MotorSystem(Constants.PINS_MOTOR);
        SensorStack = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_STACK);
        SensorDestack = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_DESTACK);
        SensorChute = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_CHUTE);
        SensorBottom = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_BOTTOM);

        Motors.SetScale(Constants.SCALING_MOTOR);
    }

    public POSITION_CAR GetPosition()
    {
        return CurrentState;
    }

    // Move car to where the new tote will be held in place by the stack holder
    public boolean GoToStack()
    {
        if (SensorStack.IsOn() || CurrentState == Constants.POSITION_CAR.STACK)
        {
            // Stop motor and advance state machine state
            Motors.Set(0);
            CurrentState = Constants.POSITION_CAR.STACK;
        }
        else
        {
            Motors.Set(Constants.MOTOR_DIRECTION_FORWARD);
        }

        // Is the car at the stack position or not?
        return CurrentState == Constants.POSITION_CAR.STACK;
    }

    // Move car to position that pushes last tote high enough to make room to
    // disengage stack holder
    public boolean GoToDestack()
    {
        if (SensorDestack.IsOn()
                || CurrentState == Constants.POSITION_CAR.DESTACK)
        {
            // Stop motor and advance state machine state
            Motors.Set(0);
            CurrentState = Constants.POSITION_CAR.DESTACK;
        }
        else
        {
            // Set motor, specifically selecting the correct direction based on
            // current position
            if (CurrentState == Constants.POSITION_CAR.STACK)
            {
                Motors.Set(Constants.MOTOR_DIRECTION_BACKWARD);
            }
            else
            {
                Motors.Set(Constants.MOTOR_DIRECTION_FORWARD);
            }
        }

        // Is the car at the destack position or not?
        return CurrentState == Constants.POSITION_CAR.DESTACK;
    }

    // Move car to position that can receive totes from chute
    public boolean GoToChute()
    {
        if (SensorChute.IsOn() || CurrentState == Constants.POSITION_CAR.CHUTE)
        {
            // Stop motor and advance state machine state
            Motors.Set(0);
            CurrentState = Constants.POSITION_CAR.CHUTE;
        }
        else
        {
            // Set motor, specifically selecting the correct direction based on
            // current position
            if (CurrentState == Constants.POSITION_CAR.BOTTOM)
            {
                Motors.Set(Constants.MOTOR_DIRECTION_FORWARD);
            }
            else
            {
                Motors.Set(Constants.MOTOR_DIRECTION_BACKWARD);
            }
        }

        // Is the car at the chute position or not?
        return CurrentState == Constants.POSITION_CAR.CHUTE;
    }

    // Move car to bottom position
    public boolean GoToBottom()
    {
        if (SensorBottom.IsOn()
                || CurrentState == Constants.POSITION_CAR.BOTTOM)
        {
            // Stop motor and advance state machine state
            Motors.Set(0);
            CurrentState = Constants.POSITION_CAR.BOTTOM;
        }
        else
        {
            Motors.Set(Constants.MOTOR_DIRECTION_BACKWARD);
        }

        // Is the car at the bottom position or not?
        return CurrentState == Constants.POSITION_CAR.BOTTOM;
    }
}