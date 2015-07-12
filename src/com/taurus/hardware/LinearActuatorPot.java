package com.taurus.hardware;

import com.taurus.MagnetoPot;
import com.taurus.Utilities;

public class LinearActuatorPot extends LinearActuator {

    private MagnetoPot Pot;
    private double DistanceFullTurn;
    private double ZeroVal;
    private double LastVal;
    private int FullTurns;
    
    // value to check against for wrapping around
    private double ThresholdVal = .5;
    
    /**
     * Create a new linear actuator using motor(s) and an encoder
     * @param MotorPins pin(s) to control the motor(s)
     * @param MotorScaling scale the speed of the motor(s) output
     * @param Positions array of positions in inches to use for setpoints, readings
     * @param PositionThreshold threshold in inches to use when determining the position
     * @param PotPin Analog input pin for the potentiometer
     * @param DistanceFullTurn distance in inches a full turn translates to
     */
    public LinearActuatorPot(int[] MotorPins, double[] MotorScaling,
            double[] Positions, double PositionThreshold,
            int PotPin, double DistanceFullTurn)
    {
        super(MotorPins, MotorScaling, Positions, PositionThreshold);
        
        Pot = new MagnetoPot(PotPin);
        this.DistanceFullTurn = DistanceFullTurn;

        ZeroVal = 0;
        LastVal = 0;
        FullTurns = 0;
    }

    
    /**
     * Create a new linear actuator using motor(s) and an encoder
     * @param MotorPins pin(s) to control the motor(s)
     * @param MotorScaling scale the speed of the motor(s) output
     * @param Positions array of positions in inches to use for setpoints, readings
     * @param PotPin Analog input pin for the potentiometer
     * @param DistanceFullTurn distance in inches a full turn translates to
     */
    public LinearActuatorPot(int[] MotorPins, double[] MotorScaling,
            double[] Positions, 
            int PotPin, double DistanceFullTurn)
    {
        super(MotorPins, MotorScaling, Positions );
        
        Pot = new MagnetoPot(PotPin);
        this.DistanceFullTurn = DistanceFullTurn;

        ZeroVal = 0;
        LastVal = 0;
        FullTurns = 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public void Zero()
    {
        ZeroVal = GetRaw();
        LastVal = 0;
        FullTurns = 0;
        super.LastPosition = 0;
    }

    /**
     * {@inheritDoc}
     */
    public double GetRaw()
    {
        return Pot.get();
    }

    /**
     * {@inheritDoc}
     */
    public double GetDistance()
    {
        // account for the zero/calibration value
        double Val = Utilities.wrapToRange(Pot.get() - ZeroVal, 0, 1);
        
        // count value roll overs
        double Diff = Val - LastVal;
        if(Diff < -ThresholdVal)
        {
            // we wrapped around from 1 to 0, so add a turn
            FullTurns++;
        }
        else if(Diff > ThresholdVal)
        {
            // we wrapped around from 0 to 1, so subtract a turn
            FullTurns--;
        }
        LastVal = Val;
        
        // add in the number of full turns, multiply by travel distance for a full turn
        return (Val + FullTurns) * DistanceFullTurn;
    }
}
