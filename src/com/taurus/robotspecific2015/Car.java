package com.taurus.robotspecific2015;

import com.taurus.Utilities;
import com.taurus.robotspecific2015.Constants.LIFT_POSITIONS_E;

import edu.wpi.first.wpilibj.Timer;

// State machine for lift
public class Car {
    
    private enum ZERO_STATE {
        MOVING,
        WAITING,
        ZEROED_TOP,
        ZEROED_BOTTOM,
    }

    private LinearActuator Actuator;
    private SensorDigital ZeroSensorLeft;
    private SensorDigital TopSensorLeft;
    private SensorDigital ZeroSensorRight;
    private SensorDigital TopSensorRight;
    
    private ZERO_STATE ZeroState = ZERO_STATE.MOVING;
    private double ZeroWaitStartTime = 0;
    private double ZeroSpeedTimer = 0;
    private double ZeroTimeoutStart = 0;
    private double ZeroTimeout = 3;

    public Car()
    {
        Actuator = new LinearActuatorPot(Constants.LIFT_MOTOR_PINS,
                Constants.LIFT_MOTOR_SCALING, Constants.LIFT_POSTITIONS,
                Constants.LIFT_THRESHOLD, Constants.LIFT_POT_PIN,
                Constants.LIFT_POT_DISTANCE);

        ZeroSensorLeft = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_ZERO_LEFT);
        TopSensorLeft = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_TOP_LEFT);

        ZeroSensorRight = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_ZERO_RIGHT);
        TopSensorRight = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_TOP_RIGHT);

        // Move the motor until you hit a sensor, then zero the encoder position
        if (ZeroSensorLeft.IsOn() || ZeroSensorRight.IsOn())
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
        ZeroTimeoutStart = 0;
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
        ZeroTimeoutStart = 0;
        Actuator.SetPosition(position.ordinal());
        return Actuator.GetPosition() == position.ordinal();
    }

    /**
     * Move car to where the new tote will be held in place by the stack holder
     * 
     * @return
     */
    public boolean GoToStack(int ToteCount)
    {
        double speedAdjust = 1.0 - Constants.LIFT_CAR_SPEED_UP;
        speedAdjust = speedAdjust * (ToteCount / 6.0);
        boolean atTop = (TopSensorLeft.IsOn() && TopSensorRight.IsOn())
                || SetPosition(LIFT_POSITIONS_E.STACK, Constants.LIFT_CAR_SPEED_UP + speedAdjust );
        
        switch (ZeroState)
        {
            default:
            case MOVING:
                if (atTop)
                {
                    ZeroState = ZERO_STATE.WAITING;
                    ZeroWaitStartTime = Timer.getFPGATimestamp();
                    Actuator.SetSpeedRaw(0);
                }
                break;
                
            case WAITING:
                Actuator.SetSpeedRaw(0);
                
                if (Timer.getFPGATimestamp() - ZeroWaitStartTime > .5)
                {
                    ZeroState = ZERO_STATE.ZEROED_TOP;
                }
                break;
                
            case ZEROED_TOP:
                Actuator.SetSpeedRaw(0);                
                
                if (!atTop)
                {
                    ZeroState = ZERO_STATE.MOVING;            
                }
                break;
        }
        
        return ZeroState == ZERO_STATE.ZEROED_TOP;
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
        boolean atBottom = ZeroSensorLeft.IsOn() || ZeroSensorRight.IsOn();
        
        if (ZeroTimeoutStart == 0)
        {
            ZeroTimeoutStart = Timer.getFPGATimestamp();
        }       
        else if (Timer.getFPGATimestamp() > ZeroTimeoutStart + ZeroTimeout )
        {
            atBottom = true;
        }
        
        if (!atBottom)
        {
            ZeroState = ZERO_STATE.MOVING;            
        }
        
        switch (ZeroState)
        {
            default:
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
                    ZeroState = ZERO_STATE.ZEROED_BOTTOM;
                }
                break;
                
            case ZEROED_BOTTOM:
                Actuator.SetSpeedRaw(0);
                ZeroTimeoutStart = 0;
                break;
        }
        
        return ZeroState == ZERO_STATE.ZEROED_BOTTOM;
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

    public boolean GoToZero()
    {   
        if (ZeroIfNeeded())
        {
            Actuator.SetSpeedRaw(0);
            Actuator.ResetError();
            ZeroSpeedTimer = 0;
            return true;
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
            
            return false;
        }
    }

    public boolean ZeroIfNeeded()
    {
        if (ZeroSensorLeft.IsOn() || ZeroSensorRight.IsOn())
        {
            Actuator.Zero();
            return true;
        }
        return false;
    }

    public Sensor GetZeroSensor()
    {
        return ZeroSensorLeft;
    }

    public void UpdateLastPosition()
    {
        Actuator.UpdateLastPosition();
    }

    public Sensor GetTopSensor()
    {
        return TopSensorLeft;
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