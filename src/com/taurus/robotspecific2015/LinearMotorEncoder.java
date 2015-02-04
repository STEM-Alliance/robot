package com.taurus.robotspecific2015;

import com.taurus.PIController;
import com.taurus.Utilities;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class LinearMotorEncoder {

    private Encoder Enc;
    private PIController EncPIController;
    private MotorSystem Motors;

    private double EncP = 1;
    private double EncI = 0;

    private double[] Positions;
    private double POSITION_THRESHOLD = .5;

    /**
     * Create a new linear encoder sensor
     * @param MotorPins pin(s) to control the motor(s)
     * @param MotorScaling scale the speed of the motor(s) output
     * @param EncoderPins 2 pins, A and B, that the encoder is connected to
     * @param InchesPerPulse distance of travel per pulse on the encoder, in inches
     * @param Positions array of positions in inches to use for setpoints, reading
     */
    public LinearMotorEncoder(int[] MotorPins, double[] MotorScaling,
            int[] EncoderPins, double InchesPerPulse, double[] Positions)
    {
        Motors = new MotorSystem(MotorPins);
        Motors.SetScale(MotorScaling);

        Enc = new Encoder(EncoderPins[0], EncoderPins[1]);
        Enc.setDistancePerPulse(InchesPerPulse);

        EncPIController = new PIController(EncP, EncI, 1.0);

        this.Positions = Positions;
    }

    /**
     * Zero the distance
     */
    public void Zero()
    {
        Enc.reset();
    }

    /**
     * Set the desired position index
     * 
     * @param Position index to the positions array to move to
     */
    public void SetPosition(int i)
    {
        double time = Timer.getFPGATimestamp();

        // use the PI to get the desired speed based on distance from current
        // position
        double speed = EncPIController.update(Positions[i], GetDistance(),
                time);

        Motors.Set(speed);
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
            if (DistanceFromPosition < POSITION_THRESHOLD)
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
     * Get the distance, in inches, from the zero point
     * 
     * @return
     */
    public double GetDistance()
    {
        return Enc.getDistance();
    }

}
