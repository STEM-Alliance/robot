package com.taurus.sensors;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class MaxBotixAnalog extends MaxBotix {
    
    // grabbed from MaxBotix datasheet, 4.88 mV == 5mm
    private static final double kmV_per_MM = .976;

    private double m_oldDistance = 0;

    /**
     * Create an instance of the Ultrasonic Sensor. This is designed to
     * supchannel the Daventech SRF04 and Vex ultrasonic sensors.
     *
     * @param pingChannel
     *            The digital output channel that sends the pulse to initiate
     *            the sensor sending the ping.
     * @param echoChannel
     *            The digital input channel that receives the echo. The length
     *            of time that the echo is high represents the round trip time
     *            of the ping, and the distance.
     * @param units
     *            The units returned in either kInches or kMilliMeters
     */
    public MaxBotixAnalog(final int pingChannel, final int echoChannel,
            Unit units)
    {
        super();
        
        m_pingChannel = new DigitalOutput(pingChannel);
        m_echoChannel = new AnalogInput(echoChannel);
        m_allocatedChannels = true;
        m_units = units;
        initialize();
    }

    /**
     * Create an instance of the Ultrasonic Sensor. This is designed to
     * supchannel the Daventech SRF04 and Vex ultrasonic sensors. Default unit
     * is inches.
     *
     * @param pingChannel
     *            The digital output channel that sends the pulse to initiate
     *            the sensor sending the ping.
     * @param echoChannel
     *            The digital input channel that receives the echo. The length
     *            of time that the echo is high represents the round trip time
     *            of the ping, and the distance.
     */
    public MaxBotixAnalog(final int pingChannel, final int echoChannel)
    {
        this(pingChannel, echoChannel, Unit.kInches);
    }

    /**
     * Create an instance of an Ultrasonic Sensor from a DigitalInput for the
     * echo channel and a DigitalOutput for the ping channel.
     *
     * @param pingChannel
     *            The digital output object that starts the sensor doing a ping.
     *            Requires a 10uS pulse to start.
     * @param echoChannel
     *            The analog input object that holds the value
     * @param units
     *            The units returned in either kInches or kMilliMeters
     */
    public MaxBotixAnalog(DigitalOutput pingChannel, AnalogInput echoChannel,
            Unit units)
    {
        super();
        
        if (pingChannel == null || echoChannel == null)
        {
            throw new NullPointerException("Null Channel Provided");
        }
        m_allocatedChannels = false;
        m_pingChannel = pingChannel;
        m_echoChannel = echoChannel;
        m_units = units;
        initialize();

    }

    /**
     * Create an instance of an Ultrasonic Sensor from a DigitalInput for the
     * echo channel and a DigitalOutput for the ping channel. Default unit is
     * inches.
     *
     * @param pingChannel
     *            The digital output object that starts the sensor doing a ping.
     *            Requires a 10uS pulse to start.
     * @param echoChannel
     *            The digital input object that times the return pulse to
     *            determine the range.
     */
    public MaxBotixAnalog(DigitalOutput pingChannel, AnalogInput echoChannel)
    {
        this(pingChannel, echoChannel, Unit.kInches);
    }

    /**
     * Initialize the Ultrasonic Sensor. This is the common code that
     * initializes the ultrasonic sensor given that there are two digital I/O
     * channels allocated. If the system was running in automatic mode (round
     * robin) when the new sensor is added, it is stopped, the sensor is added,
     * then automatic mode is restored.
     */
    protected synchronized void initialize()
    {
        super.initialize();

        
        m_conversionToInches = kmV_Per_V / kmV_per_MM / kmmPerIn;

      //  LiveWindow.addSensor("Ultrasonic",
        //        ((DigitalInput) m_echoChannel).getChannel(), this);
    }
    
    /**
     * Check if there is a valid range measurement. The ranges are accumulated
     * in a counter that will increment on each edge of the echo (return)
     * signal. If the count is not at least 2, then the range has not yet been
     * measured, and is invalid.
     *
     * @return true if the range is valid
     */
    public boolean isRangeValid()
    {
        return isRangeValid(((AnalogInput)m_echoChannel).getVoltage());
    }

    /**
     * Get the range in inches from the ultrasonic sensor.
     *
     * @return double Range in inches of the target returned from the ultrasonic
     *         sensor. If there is no valid value yet, i.e. at least one
     *         measurement hasn't completed, then return 0.
     */
    public double getRangeInches()
    {
        if (isRangeValid())
        {

            double currentInches = ((AnalogInput)m_echoChannel).getVoltage() * m_conversionToInches;

            m_oldDistance  = currentInches;
        }
        
        return m_oldDistance;
    }
}
