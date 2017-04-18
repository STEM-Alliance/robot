package org.wfrobotics.reuse.hardware.sensors;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class MaxBotixDigital extends MaxBotix {

    // grabbed from MaxBotix datasheet, 1uS == 1mm
    private static final double kuS_Per_MM = 1.0;
    
    private Counter m_counter = null;

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
    public MaxBotixDigital(final int pingChannel, final int echoChannel,
            Unit units)
    {
        super();
        
        m_pingChannel = new DigitalOutput(pingChannel);
        m_echoChannel = new DigitalInput(echoChannel);
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
    public MaxBotixDigital(final int pingChannel, final int echoChannel)
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
     *            The digital input object that times the return pulse to
     *            determine the range.
     * @param units
     *            The units returned in either kInches or kMilliMeters
     */
    public MaxBotixDigital(DigitalOutput pingChannel, DigitalInput echoChannel,
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
    public MaxBotixDigital(DigitalOutput pingChannel, DigitalInput echoChannel)
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

        m_conversionToInches = kuS_Per_S / kuS_Per_MM / kmmPerIn;
        
        m_counter = new Counter((DigitalSource) m_echoChannel); // set up
                                                                // counter for
                                                                // this sensor
        m_counter.setMaxPeriod(1.0);
        m_counter.setSemiPeriodMode(true);
        m_counter.reset();

        LiveWindow.addSensor("Ultrasonic",
                ((DigitalInput) m_echoChannel).getChannel(), this);
        
        super.initialize();
    }

    /**
     * Destructor for the ultrasonic sensor. Delete the instance of the
     * ultrasonic sensor by freeing the allocated digital channels. If the
     * system was in automatic mode (round robin), then it is stopped, then
     * started again after this sensor is removed (provided this wasn't the last
     * sensor).
     */
    public synchronized void free()
    {
        if (m_counter != null)
        {
            m_counter.free();
            m_counter = null;
        }

        super.free();
    }

    /**
     * Single ping to ultrasonic sensor. Send out a single ping to the
     * ultrasonic sensor. This only works if automatic (round robin) mode is
     * disabled. A single ping is sent out, and the counter should count the
     * semi-period when it comes in. The counter is reset to make the current
     * value invalid.
     */
    public void ping()
    {
        m_counter.reset(); // reset the counter to zero (invalid data now)
        super.ping();
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
        if(m_counter.get() > 1)
        {
            return isRangeValid(m_counter.getPeriod());
        }
        else
        {
            return false;
        }
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

            double currentInches = m_counter.getPeriod() * m_conversionToInches;

            m_oldDistance  = currentInches;
        }
        
        return m_oldDistance;
    }


    public void setConversionToInches(double converter)
    {
        m_conversionToInches = converter;
    }

    public void reset()
    {
        m_counter.reset();
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public PIDSourceType getPIDSourceType()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
