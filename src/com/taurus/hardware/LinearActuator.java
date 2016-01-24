package com.taurus.hardware;

import com.taurus.PIDController;
import com.taurus.Utilities;

/**
 * A Linear actuator subsystem using motor(s) and an encoder
 *
 */
public abstract class LinearActuator {

    private PIDController ActuatorPIController;
    private double ActP = 2.0 / 5.0;  // full speed at 3 in error
    private double ActI = 0.8;
    private double ActD = 0;
    
    private MotorSystem Motors;

    protected int LastPosition;
    private double[] Positions;
    private double PositionThreshold;
    private static final double PositionThresholdDefault = 0.5;


    public LinearActuator(int[] MotorPins, double[] MotorScaling,
            double[] Positions, double PositionThreshold)
    {
        Motors = new MotorSystem(MotorPins);
        Motors.SetScale(MotorScaling);
        Motors.SetSafety(true, 2);

        this.Positions = Positions;
        this.PositionThreshold = PositionThreshold;
        
        ActuatorPIController = new PIDController(ActP, ActI, ActD, 1.0);
        LastPosition = 0;
    }


    /**
     * Create a new linear actuator using motor(s) and an encoder with the default
     * position threshold of .5
     * @param MotorPins pin(s) to control the motor(s)
     * @param MotorScaling scale the speed of the motor(s) output
     * @param EncoderPins 2 pins, A and B, that the encoder is connected to
     * @param InchesPerPulse distance of travel per pulse on the encoder, in inches
     * @param Positions array of positions in inches to use for setpoints, readings
     */
    public LinearActuator(int[] MotorPins, double[] MotorScaling,
            double[] Positions)
    {
        this(MotorPins, MotorScaling, Positions, PositionThresholdDefault);
    }

    /**
     * Zero the distance
     */
    public abstract void Zero();

    public void UpdateLastPosition()
    {
        SetPosition(LastPosition);
    }
    
    public void ResetError()
    {
        ActuatorPIController.integral = 0;
        ActuatorPIController.lastError = 0;
    }
    
    /**
     * Set the desired position index
     * 
     * @param Position index to the positions array to move to
     */
    public void SetPosition(int i, double MaxSpeed)
    {        
        Positions[i] = com.taurus.Application.prefs.getDouble("Positions_" + i + "", Positions[i]);

        // use the PI to get the desired speed based on distance from current
        // position
        double speed = ActuatorPIController.update(Positions[i], GetDistance());
        
        MaxSpeed = Utilities.clampToRange(MaxSpeed, -1, 1);
        
        speed = Utilities.clampToRange(speed, -MaxSpeed, MaxSpeed);
        
        Motors.Set(speed);
        
        LastPosition = i;
    }
    
    /**
     * Set the desired position index
     * 
     * @param Position index to the positions array to move to
     */
    public void SetPosition(int i)
    {
        SetPosition(i, 1.0);
    }

    /**
     * Get the current position index if it is close enough to a position index,
     * otherwise returns -1
     * 
     * @return Position index if close to a position, else -1
     */
    public int GetPosition()
    {
        int position = -1;

        double CurrentDistance = GetDistance();

        for (int i = 0; i < Positions.length; i++)
        {
            // first get the distance from current to the test point
            double DistanceFromPosition = Math.abs(CurrentDistance
                    - Positions[i]);

            // then see if that distance is close enough using the threshold
            if (DistanceFromPosition < PositionThreshold)
            {
                position = i;
                break;
            }
        }

        return position;
    }

    /**
     * Get the current position index, but if it isn't with the threshold of a
     * position, return the ratio between 0 and 1 of the distance between the 2
     * nearest positions
     * 
     * @return position as a double
     */
    public double GetPositionRaw()
    {
        double position = GetPosition();

        if (position == -1)
        {
            double CurrentDistance = GetDistance();

            // we aren't close to a position, so get the range
            for (int i = 1; i < (Positions.length); i++)
            {
                if (Utilities.isBetween(CurrentDistance, Positions[i - 1],
                        Positions[i]))
                {
                    // scale the position from inches to the ratio between
                    // positions
                    position = Utilities.scaleToRange(CurrentDistance,
                            Positions[i - 1], Positions[i], i - 1, i);
                    break;
                }

            }
        }

        return position;
    }
    
    /**
     * Set the desired speed directly on the motors
     * 
     * @param speed - raw value between -1 and 1
     */
    public void SetSpeedRaw(double speed)
    {
        Motors.Set(speed);
    }

    /**
     * Get the raw value of the sensor
     * 
     * @return
     */
    public abstract double GetRaw();
    
    /**
     * Get the distance, in inches, from the zero point
     * 
     * @return
     */
    public abstract double GetDistance();

}
