package com.taurus.robotspecific2015;

import com.taurus.Utilities;
import com.taurus.robotspecific2015.Constants.LIFT_POSITIONS_E;

import edu.wpi.first.wpilibj.Timer;

// State machine for lift
public class Car {
    
    private enum ZERO_STATE {
        MOVING,
        WAITING,
        ZEROED
    }

    private LinearActuator Actuator;
    private SensorDigital ZeroSensor;
    private SensorDigital TopSensor;
    
    private ZERO_STATE ZeroState = ZERO_STATE.MOVING;
    private double ZeroWaitStartTime = 0;
    private double ZeroSpeedTimer = 0;

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

    /**
     * Move car to where the new tote will be held in place by the stack holder
     * 
     * @return
     */
    public boolean GoToStack()
    {
        boolean atTop = TopSensor.IsOn() || SetPosition(LIFT_POSITIONS_E.STACK, Constants.LIFT_CAR_SPEED_UP);
        
        if (!atTop)
        {
            ZeroState = ZERO_STATE.MOVING;            
        }
        
        switch (ZeroState)
        {
            case MOVING:
                if (atTop)
                {
                    ZeroState = ZERO_STATE.WAITING;
                    ZeroWaitStartTime = Timer.getFPGATimestamp();
                    Actuator.SetSpeedRaw(0);
                }
                break;
                
            case WAITING:
                if (Timer.getFPGATimestamp() - ZeroWaitStartTime > .5)
                {
                    ZeroState = ZERO_STATE.ZEROED;
                }
                break;
                
            case ZEROED:
                break;
        }
        
        return ZeroState == ZERO_STATE.ZEROED;
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
            return SetPosition(LIFT_POSITIONS_E.DESTACK,
                    Constants.LIFT_CAR_SPEED_UP);
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
            return SetPosition(LIFT_POSITIONS_E.CHUTE,
                    Constants.LIFT_CAR_SPEED_DOWN);
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
        if (Application.controller.getManualLift())
        {
            return Application.controller.getFakePostion();
        }
        else
        {
            return SetPosition(LIFT_POSITIONS_E.CONTAINER_STACK,
                    Constants.LIFT_CAR_SPEED_DOWN);
        }
    }

    /**
     * Move car to bottom position
     * 
     * @return
     */
    public boolean GoToBottom()
    {   
        boolean atBottom = ZeroSensor.IsOn();
        
        if (!atBottom)
        {
            ZeroState = ZERO_STATE.MOVING;            
        }
        
        switch (ZeroState)
        {
            case MOVING:
                GoToZero();
                
                if (atBottom)
                {
                    Actuator.SetSpeedRaw(0);
                    ZeroState = ZERO_STATE.WAITING;
                    ZeroWaitStartTime = Timer.getFPGATimestamp();
                }
                break;
                
            case WAITING:
                Actuator.SetSpeedRaw(0);
                
                if (Timer.getFPGATimestamp() - ZeroWaitStartTime > .25)
                {
                    ZeroState = ZERO_STATE.ZEROED;
                }
                break;
                
            case ZEROED:
                Actuator.SetSpeedRaw(0);
                break;
        }
        
        return ZeroState == ZERO_STATE.ZEROED;
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
        Actuator.SetSpeedRaw(Constants.LIFT_CAR_SPEED_UP);
    }

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
            if (Timer.getFPGATimestamp() - ZeroSpeedTimer < Constants.LIFT_CAR_TIME_DOWN_INITIAL)
            {
                Actuator.SetSpeedRaw(-Constants.LIFT_CAR_SPEED_DOWN_INITIAL);
            }
            else if (Timer.getFPGATimestamp() - ZeroSpeedTimer
                    - Constants.LIFT_CAR_TIME_DOWN_INITIAL < Constants.LIFT_CAR_TIME_DOWN_INCREASING)
            {
                Actuator.SetSpeedRaw(-Utilities.scaleToRange(
                        Timer.getFPGATimestamp(), 
                        ZeroSpeedTimer + Constants.LIFT_CAR_TIME_DOWN_INITIAL,
                        ZeroSpeedTimer + Constants.LIFT_CAR_TIME_DOWN_INITIAL + Constants.LIFT_CAR_TIME_DOWN_INCREASING,
                        Constants.LIFT_CAR_SPEED_DOWN_INITIAL,
                        Constants.LIFT_CAR_SPEED_DOWN));
            }
            else
            {
                Actuator.SetSpeedRaw(-Constants.LIFT_CAR_SPEED_DOWN);
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

    public boolean GoToEject()
    {
        if (Application.controller.getManualLift())
        {
            return Application.controller.getFakePostion();
        }
        else
        {
            return SetPosition(LIFT_POSITIONS_E.EJECT, Constants.LIFT_CAR_SPEED_DOWN_INITIAL);
        }
    }
}