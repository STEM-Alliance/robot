package com.taurus.hardware;

import edu.wpi.first.wpilibj.Encoder;

public class LinearActuatorEncoder extends LinearActuator {

    private Encoder Enc;
    
    /**
     * Create a new linear actuator using motor(s) and an encoder
     * @param MotorPins pin(s) to control the motor(s)
     * @param MotorScaling scale the speed of the motor(s) output
     * @param Positions array of positions in inches to use for setpoints, readings
     * @param PositionThreshold threshold in inches to use when determining the position
     * @param EncoderPins 2 pins, A and B, that the encoder is connected to
     * @param InchesPerPulse distance of travel per pulse on the encoder, in inches
     */
    public LinearActuatorEncoder(int[] MotorPins, MotorSystem.TYPES[] MotorTypes, double MotorScaling,
            double[] Positions, double PositionThreshold,
            int[] EncoderPins, double InchesPerPulse)
    {
        super(MotorPins, MotorTypes, MotorScaling, Positions, PositionThreshold);

        Enc = new Encoder(EncoderPins[0], EncoderPins[1]);
        Enc.setDistancePerPulse(InchesPerPulse);
    }
    /**
     * Create a new linear actuator using motor(s) and an encoder with the default
     * position threshold of .5
     * @param MotorPins pin(s) to control the motor(s)
     * @param MotorScaling scale the speed of the motor(s) output
     * @param Positions array of positions in inches to use for setpoints, readings
     * @param EncoderPins 2 pins, A and B, that the encoder is connected to
     * @param InchesPerPulse distance of travel per pulse on the encoder, in inches
     */
    public LinearActuatorEncoder(int[] MotorPins, MotorSystem.TYPES[] MotorTypes, double MotorScaling,
            double[] Positions, int[] EncoderPins, double InchesPerPulse)
    {
        super(MotorPins, MotorTypes, MotorScaling, Positions);

        Enc = new Encoder(EncoderPins[0], EncoderPins[1]);
        Enc.setDistancePerPulse(InchesPerPulse);
    }

    /**
     * {@inheritDoc}
     */
    public void Zero()
    {
        Enc.reset();
    }

    /**
     * {@inheritDoc}
     */
    public double GetRaw()
    {
        return Enc.getRaw();
    }
    
    /**
     * {@inheritDoc}
     */
    public double GetDistance()
    {
        return Enc.getDistance();
    }
}
