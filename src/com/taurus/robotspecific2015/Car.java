package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.POSITION_CAR;

// State machine for lift
public class Car {

    LinearMotorEncoder MotorEncoder;
    SensorDigital ZeroSensor;

    public Car()
    {
        MotorEncoder = new LinearMotorEncoder(Constants.PINS_LIFT_MOTOR,
                Constants.SCALING_LIFT_MOTOR, Constants.LIFT_ENCODER_PINS, Constants.INCHES_PER_PULSE,
                Constants.LIFT_POSTITIONS);
        ZeroSensor = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_ZERO);

        // Move the motor until you hit a sensor, then zero the encoder position
        if (ZeroSensor.IsOn())
        {
            MotorEncoder.Zero();
        }
        else
        {
            MotorEncoder.SetSpeedRaw(Constants.MOTOR_DIRECTION_BACKWARD);
        }
    }

    public POSITION_CAR GetPosition()
    {
        POSITION_CAR result = POSITION_CAR.MOVING;
        int position = MotorEncoder.GetPosition();

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

    // Move car to where the new tote will be held in place by the stack holder
    public boolean GoToStack()
    {
        MotorEncoder.SetPosition(3);
        return MotorEncoder.GetPosition() == 3;
    }

    // Move car to position that pushes last tote high enough to make room to
    // disengage stack holder
    public boolean GoToDestack()
    {
        MotorEncoder.SetPosition(2);
        return MotorEncoder.GetPosition() == 2;
    }

    // Move car to position that can receive totes from chute
    public boolean GoToChute()
    {
        MotorEncoder.SetPosition(1);
        return MotorEncoder.GetPosition() == 1;
    }

    // Move car to bottom position
    public boolean GoToBottom()
    {
        MotorEncoder.SetPosition(0);
        return MotorEncoder.GetPosition() == 0;
    }
}