package com.taurus.robotspecific2015;

import com.taurus.Utilities;
import com.taurus.controller.Controller;
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
    private SensorDigital ChuteHeightSensor;
    
    private ZERO_STATE ZeroState = ZERO_STATE.MOVING;
    private double ZeroWaitStartTime = 0;
    private double ZeroSpeedTimer = 0;
    private double ZeroTimeoutStart = 0;
    private double ZeroTimeout = 3;
    private double ShakeStartTime = Timer.getFPGATimestamp();
    private boolean IsShakeUp = true;  // Shake down first because ShakeStartTime will have elapsed     
    
    private final Controller controller;

    /**
     * Constructor for the car
     * @param controller main controller object
     */
    public Car(Controller controller)
    {
        this.controller = controller;
        
        Actuator = new LinearActuatorPot(Constants.LIFT_MOTOR_PINS,
                Constants.LIFT_MOTOR_SCALING, Constants.LIFT_POSTITIONS,
                Constants.LIFT_THRESHOLD, Constants.LIFT_POT_PIN,
                Constants.LIFT_POT_DISTANCE);

        ZeroSensorLeft = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_ZERO_LEFT);
        TopSensorLeft = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_TOP_LEFT);

        ZeroSensorRight = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_ZERO_RIGHT);
        TopSensorRight = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_TOP_RIGHT);
        
        ChuteHeightSensor = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_CHUTE);

        // If we're at the bottom when initializing, zero the actuator
        if (ZeroSensorLeft.IsOn() || ZeroSensorRight.IsOn())
        {
            Actuator.Zero();
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
     * @param MaxSpeed maximum speed to drive the lift at
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
     * @return true if we're at position
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
     * @return true when finished
     */
    public boolean GoToStack(int ToteCount, boolean ContainerInStack)
    {
        double containerLiftSpeed = Application.prefs.getDouble("Lift_Speed_Container", .8);
        boolean finished = false;
        
        double speedAdjust = 1.0 - Constants.LIFT_CAR_SPEED_UP;
        speedAdjust = speedAdjust * (ToteCount / 6.0);
        boolean atTop = (TopSensorLeft.IsOn() && TopSensorRight.IsOn());
        
        
        if(ContainerInStack){
            SetPosition(LIFT_POSITIONS_E.STACK, (Constants.LIFT_CAR_SPEED_UP + speedAdjust) * containerLiftSpeed);
        } else{
            SetPosition(LIFT_POSITIONS_E.STACK, Constants.LIFT_CAR_SPEED_UP + speedAdjust );
        }
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
                
                if (Timer.getFPGATimestamp() - ZeroWaitStartTime > 0)
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
                else
                {
                    finished = true;
                }
                break;
        }
        
        return finished;
    }

    /**
     * Move car to position that pushes last tote high enough to make room to
     * disengage stack holder
     * 
     * @return
     */
    public boolean GoToDestack(boolean ContainerInStack)
    {
        double containerLiftSpeed = Application.prefs.getDouble("Lift_Speed_Drop_Container", .8);

        
        if (controller.getManualLift())
        {
            return controller.getFakePostion();
        }
        else
        {
            
            if(ContainerInStack){
                return SetPosition(LIFT_POSITIONS_E.DESTACK, (Constants.LIFT_CAR_SPEED_UP) * containerLiftSpeed);
            } else{
                return SetPosition(LIFT_POSITIONS_E.DESTACK, Constants.LIFT_CAR_SPEED_UP);
            }
        }

    }

    /**
     * Move car to position that can receive totes from chute
     * 
     * @return
     */
    public boolean GoToChute(boolean lowSpeed)
    {
        if (controller.getManualLift())
        {
            return controller.getFakePostion();
        }
        else
        {
            if(lowSpeed)
            {
                return SetPosition(LIFT_POSITIONS_E.CHUTE,
                            Constants.LIFT_CAR_SPEED_DOWN) 
                        || this.ChuteHeightSensor.IsOn();
            }
            else
            {
                return SetPosition(LIFT_POSITIONS_E.CHUTE,
                            Constants.LIFT_CAR_SPEED_DOWN * .75) 
                        || this.ChuteHeightSensor.IsOn();
            }
        }
    }

    /**
     * Move car to position that can grab containers
     * 
     * @return
     */
    public boolean GoToContainerGrab()
    {
        if (controller.getManualLift())
        {
            return controller.getFakePostion();
        }
        else
        {
            return GoToBottom(6);
        }

    }

    /**
     * Move car to position that can stack containers
     * 
     * @return
     */
    public boolean GoToContainerStack()
    {
        if (controller.getManualLift())
        {
            return controller.getFakePostion();
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
     * @return true if we're at the bottom
     */
    public boolean GoToBottom(int TotesInStack)
    {   
        boolean atBottom = ZeroSensorLeft.IsOn() || ZeroSensorRight.IsOn();
        boolean done = false;
        
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
        
        // start moving/waiting
        switch (ZeroState)
        {
            default:
            case MOVING:
                // start moving down
                GoToZero(TotesInStack);
                
                if (atBottom)
                {
                    // we're at the bottom, so stop moving
                    Actuator.SetSpeedRaw(0);
                    ZeroState = ZERO_STATE.WAITING;
                    ZeroWaitStartTime = Timer.getFPGATimestamp();
                }
                break;
                
            case WAITING:
                // wait a little bit to ensure we resync/zero properly
                Actuator.SetSpeedRaw(0);
                
                if (Timer.getFPGATimestamp() - ZeroWaitStartTime > .3)
                {
                    ZeroState = ZERO_STATE.ZEROED_BOTTOM;
                }
                break;
                
            case ZEROED_BOTTOM:
                // we're done
                Actuator.SetSpeedRaw(0);
                ZeroTimeoutStart = 0;
                done = true;
                break;
        }
        
        return done;
    }

    /**
     * Get the current height of the car in inches
     * 
     * @return height in inches
     */
    public double GetHeight()
    {
        return Actuator.GetDistance();
    }

    /**
     * Go to the top of the lift. Will continue driving upwards even if switches are triggered.
     * This allows us to resync the two rails
     */
    public void GoUp()
    {
        Actuator.SetSpeedRaw(Constants.LIFT_CAR_SPEED_UP);
    }

    /**
     * Go to the zero position (bottom). Will start slow and ramp speed as time increases
     * @return true if we're at the bottom, else false
     */
    public boolean GoToZero(int TotesInStack)
    {
        if (ZeroIfNeeded())
        {
            // if we're at the bottom, we're done
            Actuator.SetSpeedRaw(0);
            Actuator.ResetError();
            ZeroSpeedTimer = 0;
            return true;
        }
        else
        {
            //start the timer in order to start slow, and then speed up
            if (ZeroSpeedTimer == 0)
            {
                ZeroSpeedTimer = Timer.getFPGATimestamp();
            }
            
            if (Timer.getFPGATimestamp() - ZeroSpeedTimer < Constants.LIFT_CAR_TIME_DOWN_INITIAL)
            {
                // initial speed
                Actuator.SetSpeedRaw(-Constants.LIFT_CAR_SPEED_DOWN_INITIAL );
            }
            else if (Timer.getFPGATimestamp() - ZeroSpeedTimer
                    - Constants.LIFT_CAR_TIME_DOWN_INITIAL < Constants.LIFT_CAR_TIME_DOWN_INCREASING)
            {
                // increase as time increases
                Actuator.SetSpeedRaw(-Utilities.scaleToRange(
                        Timer.getFPGATimestamp(), 
                        ZeroSpeedTimer + Constants.LIFT_CAR_TIME_DOWN_INITIAL,
                        ZeroSpeedTimer + Constants.LIFT_CAR_TIME_DOWN_INITIAL + Constants.LIFT_CAR_TIME_DOWN_INCREASING,
                        Constants.LIFT_CAR_SPEED_DOWN_INITIAL,
                        1.0));
            }
            else
            {
                // at full speed
                Actuator.SetSpeedRaw(-1.0);
            }
            
            return false;
        }
    }

    /**
     * Zero the actuator if either switch is pressed
     * @return true if we zeroed at the bottom, else false
     */
    public boolean ZeroIfNeeded()
    {
        // if either zero sensor is triggered, zero the height of the actuator
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

    /**
     * Go to the eject height
     * 
     * @return true if we're at the position, else false
     */
    public boolean GoToEject()
    {
        if (controller.getManualLift())
        {
            return controller.getFakePostion();
        }
        else
        {
            return SetPosition(LIFT_POSITIONS_E.EJECT, Constants.LIFT_CAR_SPEED_DOWN_INITIAL);
        }
    }
    
    // Dislodge a stuck tote by jittering the car
    public void ShakeCar()
    {
        // Is it time to reverse the direction?

        if (IsShakeUp)
        {
            if((Timer.getFPGATimestamp() - ShakeStartTime > Constants.LIFT_CAR_TIME_SHAKE_UP)
                    /*|| (ZeroSensorLeft.IsOn() || ZeroSensorRight.IsOn())*/)
            {
                // Reverse direction and reset timer
                Actuator.SetSpeedRaw(Constants.LIFT_CAR_SPEED_SHAKE_DOWN);
                ShakeStartTime = Timer.getFPGATimestamp();
                IsShakeUp = !IsShakeUp;  // Reverse state for next time
            }
        }
        else
        {
            if((Timer.getFPGATimestamp() - ShakeStartTime > Constants.LIFT_CAR_TIME_SHAKE_DOWN)
                    /*|| (ZeroSensorLeft.IsOn() || ZeroSensorRight.IsOn())*/)
            {
            // Reverse direction and reset timer
                Actuator.SetSpeedRaw(Constants.LIFT_CAR_SPEED_SHAKE_UP);
                ShakeStartTime = Timer.getFPGATimestamp();
                IsShakeUp = !IsShakeUp;  // Reverse state for next time
            }
        }
    }
}