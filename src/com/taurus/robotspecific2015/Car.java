package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.LIFT_POSITIONS_E;

import edu.wpi.first.wpilibj.Timer;

// State machine for lift
public class Car {

    private LinearActuator Actuator;
    private SensorDigital ZeroSensor;
    private SensorDigital TopSensor;

    public Car()
    {
        Actuator = new LinearActuatorPot(Constants.LIFT_MOTOR_PINS,
                Constants.LIFT_MOTOR_SCALING, Constants.LIFT_POSTITIONS,
                Constants.LIFT_THRESHOLD, Constants.LIFT_POT_PIN,
                Constants.LIFT_POT_DISTANCE);

        ZeroSensor = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_ZERO);
        TopSensor = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_TOP);

        // Move the motor until you hit a sensor, then zero the encoder position
        if (ZeroSensor.IsOn())
        {
            Actuator.Zero();
        }
        else
        {
            // Actuator.SetSpeedRaw(Constants.MOTOR_DIRECTION_BACKWARD);
        }
    }

    public LinearActuator GetActuator()
    {
        return Actuator;
    }

    /**
     * Get the position of the car
     * 
     * @return
     */
    public LIFT_POSITIONS_E GetPosition()
    {
        return LIFT_POSITIONS_E.fromInt(Actuator.GetPosition());
    }

    /**
     * Go to position
     * 
     * @param position
     *            position enum val
     * @return
     */
    public boolean SetPosition(LIFT_POSITIONS_E position, double MaxSpeed)
    {
        Actuator.SetPosition(position.ordinal(), MaxSpeed);
        return Actuator.GetPosition() == position.ordinal();
    }

    /**
     * Go to position
     * 
     * @param position
     *            position enum val
     * @return
     */
    public boolean SetPosition(LIFT_POSITIONS_E position)
    {
        Actuator.SetPosition(position.ordinal());
        return Actuator.GetPosition() == position.ordinal();
    }

    private double TopTime = 0;
    private boolean waiting = false;

    /**
     * Move car to where the new tote will be held in place by the stack holder
     * 
     * @return
     */
    public boolean GoToStack()
    {
        if (waiting)
        {
            if (Timer.getFPGATimestamp() - TopTime > .5)
            {
                waiting = false;
                return true;
            }
            return false;
        }
        else if (TopSensor.IsOn())
        {
            waiting = true;
            TopTime = Timer.getFPGATimestamp();
            Actuator.SetSpeedRaw(0);
            return false;
        }
        else
        {
            SetPosition(LIFT_POSITIONS_E.STACK, .7);
            return false;
        }

        // if(TopSensor.IsOn() || waiting)
        // {
        // waiting = true;
        // Actuator.SetSpeedRaw(0);
        //
        // if(Timer.getFPGATimestamp() - TopTime > .5)
        // {
        // return true;
        // }
        // return false;
        // }
        // else
        // {
        // TopTime = Timer.getFPGATimestamp();
        // SetPosition(LIFT_POSITIONS_E.STACK, .7);
        // return false;
        // }
    }

    /**
     * Move car to position that pushes last tote high enough to make room to
     * disengage stack holder
     * 
     * @return
     */
    public boolean GoToDestack()
    {
        if (Application.controller.getManualLift())
        {
            return Application.controller.getFakePostion();
        }
        else
        {
            return SetPosition(LIFT_POSITIONS_E.DESTACK, .7);

        }

    }

    /**
     * Move car to position that can receive totes from chute
     * 
     * @return
     */
    public boolean GoToChute()
    {
        if (Application.controller.getManualLift())
        {
            return Application.controller.getFakePostion();
        }
        else
        {
            return SetPosition(LIFT_POSITIONS_E.CHUTE, .7);

        }
    }

    /**
     * Move car to position that can grab containers
     * 
     * @return
     */
    public boolean GoToContainerGrab()
    {
        if (Application.controller.getManualLift())
        {
            return Application.controller.getFakePostion();
        }
        else
        {
            return GoToBottom();

        }

    }

    /**
     * Move car to position that can stack containers
     * 
     * @return
     */
    public boolean GoToContainerStack()
    {
        return SetPosition(LIFT_POSITIONS_E.CONTAINER_STACK, .7);
    }

    /**
     * Move car to bottom position
     * 
     * @return
     */
    public boolean GoToBottom()
    {
        GoToZero();
        return ZeroIfNeeded();
    }

    /**
     * Get the current height of the car in inches
     * 
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

    private double ZeroSpeedTimer = 0;

    public void GoToZero()
    {
        if (ZeroIfNeeded())
        {
            Actuator.SetSpeedRaw(0);
            Actuator.ResetError();
            ZeroSpeedTimer = 0;
        }
        else
        {
            if (ZeroSpeedTimer == 0)
            {
                ZeroSpeedTimer = Timer.getFPGATimestamp();
            }
            if (Timer.getFPGATimestamp() - ZeroSpeedTimer < .3)
            {
                Actuator.SetSpeedRaw(-.3);
            }
            else if (Timer.getFPGATimestamp() - ZeroSpeedTimer < .5)
            {
                Actuator.SetSpeedRaw(-.4);
            }
            else if (Timer.getFPGATimestamp() - ZeroSpeedTimer < .7)
            {
                Actuator.SetSpeedRaw(-.5);
            }
            else
            {
                Actuator.SetSpeedRaw(-.6);
            }
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

    public Sensor GetTopSensor()
    {
        return TopSensor;
    }
}