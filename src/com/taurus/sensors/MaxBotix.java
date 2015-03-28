/**
 * 
 */
package com.taurus.sensors;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * Ultrasonic rangefinder class for MaxBotix HRLV-MaxSonar-EZ sensors. Similar to
 * the built in Ultrasonic class, but auto converts the distances based on the
 * datasheet for MaxBotix sensors. Provides analog/digital options
 */
public abstract class MaxBotix extends SensorBase implements PIDSource,
        LiveWindowSendable {

    /**
     * The units to return when PIDGet is called
     */
    public static class Unit {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kInches_val = 0;
        static final int kMillimeters_val = 1;
        /**
         * Use inches for PIDGet
         */
        public static final Unit kInches = new Unit(kInches_val);
        /**
         * Use millimeters for PIDGet
         */
        public static final Unit kMillimeter = new Unit(kMillimeters_val);

        private Unit(int value) {
            this.value = value;
        }
    }
    
    protected static final double kMaxUltrasonicTime = .075;
    
    protected static final double kmmPerIn = 25.4;
    protected static final double kuS_Per_S = 1000000.0;
    protected static final double kmV_Per_V = 1000.0;
    
    protected double m_conversionToInches;

    protected static double m_pingTime = 10 * 1e-6; // Time (sec) for the ping trigger pulse.
    protected static int m_priority = 90; // Priority that the ultrasonic round robin task runs.
    
    protected static MaxBotix m_firstSensor = null; // head of the ultrasonic
    protected MaxBotix m_nextSensor = null;
    
    protected static boolean m_automaticEnabled = false; // automatic round robin // mode
    
    protected SensorBase m_echoChannel = null;
    protected DigitalOutput m_pingChannel = null;
    
    protected boolean m_allocatedChannels;
    
    protected boolean m_enabled = false;
    
    protected double m_currentAverageDistance = 0;
    
    protected static Thread m_task = null; // task doing the round-robin automatic
                                            // sensing
    protected Unit m_units;
    
    protected static int m_instances = 0;
    
    protected int m_distanceBufferLength = 4;
    protected int m_distanceBufferIndex = 0;
    protected double m_distanceBufferInches[];

    protected double m_minRange = 10;

    protected double m_maxRange = 400;

    public MaxBotix()
    {
    }
    
    public MaxBotix(int AveragingBufferLength)
    {
        m_distanceBufferLength = AveragingBufferLength;
    }
    
    /**
     * Background task that goes through the list of ultrasonic sensors and
     * pings each one in turn. The counter is configured to read the timing of
     * the returned echo pulse.
     *
     * DANGER WILL ROBINSON, DANGER WILL ROBINSON: This code runs as a task and
     * assumes that none of the ultrasonic sensors will change while it's
     * running. If one does, then this will certainly break. Make sure to
     * disable automatic mode before changing anything with the sensors!!
     */
    private class UltrasonicChecker extends Thread
    {

        public synchronized void run()
        {
            MaxBotix u = null;
            
            while (m_automaticEnabled)
            {
                if (u == null)
                {
                    u = m_firstSensor;
                }
                
                if (u == null)
                {
                    return;
                }
                
                if (u.isEnabled())
                {
                    u.m_pingChannel.pulse(m_pingChannel.getChannelForRouting(),
                            (float) m_pingTime); // do the ping
                }
                
                Timer.delay(kMaxUltrasonicTime); // wait for ping to return
                
                // now add the current value to the circular buffer
                if(u.m_distanceBufferIndex >= u.m_distanceBufferLength)
                {
                    u.m_distanceBufferIndex = 0;
                }

                // using m_distanceBuffer as a circular buffer here
                u.m_distanceBufferInches[u.m_distanceBufferIndex++] = u.getRangeInches();
                
                // move to next sensor
                u = u.m_nextSensor;
            }
        }
    }
    
    /**
     * 
     * @param pingTime
     */
    public static void setPingTime(double pingTime)
    {
        m_pingTime = pingTime;
    }
    
    public static void setThreadPriority(int newPriority)
    {
        m_priority = newPriority;
        m_task.setPriority(m_priority);
    }

    public void setConversionToInches(double converter)
    {
        m_conversionToInches = converter;
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
        if (m_task == null)
        {
            m_task = new UltrasonicChecker();
        }
        
        boolean originalMode = m_automaticEnabled;
        
        setAutomaticMode(false); // kill task when adding a new sensor
        
        m_nextSensor = m_firstSensor;
        m_firstSensor = this;

        m_enabled = true; // make it available for round robin scheduling
        setAutomaticMode(originalMode);

        m_instances++;
        UsageReporting.report(tResourceType.kResourceType_Ultrasonic, m_instances);
        
        m_distanceBufferInches = new double[m_distanceBufferLength];
        
        m_enabled = true;
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
        boolean wasAutomaticMode = m_automaticEnabled;
        setAutomaticMode(false);
        if (m_allocatedChannels)
        {
            if (m_pingChannel != null)
            {
                m_pingChannel.free();
            }
            if (m_echoChannel != null)
            {
                m_echoChannel.free();
            }
        }

        m_pingChannel = null;
        m_echoChannel = null;

        if (this == m_firstSensor)
        {
            m_firstSensor = m_nextSensor;
            if (m_firstSensor == null)
            {
                setAutomaticMode(false);
            }
        }
        else
        {
            for (MaxBotix s = m_firstSensor; s != null; s = s.m_nextSensor)
            {
                if (this == s.m_nextSensor)
                {
                    s.m_nextSensor = s.m_nextSensor.m_nextSensor;
                    break;
                }
            }
        }

        if (m_firstSensor != null && wasAutomaticMode)
        {
            setAutomaticMode(true);
        }
    }
    
    /**
     * Turn Automatic mode on/off. When in Automatic mode, all sensors will fire
     * in round robin, waiting a set time between each sensor.
     *
     * @param enabling
     *            Set to true if round robin scheduling should start for all the
     *            ultrasonic sensors. This scheduling method assures that the
     *            sensors are non-interfering because no two sensors fire at the
     *            same time. If another scheduling algorithm is preffered, it
     *            can be implemented by pinging the sensors manually and waiting
     *            for the results to come back.
     */
    public void setAutomaticMode(boolean enabling)
    {
        if (enabling == m_automaticEnabled)
        {
            return; // ignore the case of no change
        }
        
        m_automaticEnabled = enabling;

        if (enabling)
        {
            // enabling automatic mode.
            // Clear all the counters so no data is valid
            for (MaxBotix u = m_firstSensor; u != null; u = u.m_nextSensor)
            {
                this.reset();
            }
            // Start round robin task
            m_task.start();
        }
        else
        {
            // disabling automatic mode. Wait for background task to stop
            // running.
            while (m_task.isAlive())
            {
                Timer.delay(kMaxUltrasonicTime * 1.5); // just a little longer than the ping time for
                                    // round-robin to stop
            }
            // clear all the counters (data now invalid) since automatic mode is
            // stopped
            for (MaxBotix u = m_firstSensor; u != null; u = u.m_nextSensor)
            {
                this.reset();
            }
        }
    }
    
    public void reset()
    {
        double inches = getRangeInches();
        for (int i = 0; i < m_distanceBufferLength; i++)
        {
            m_distanceBufferInches[i] = inches;
        }
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
        setAutomaticMode(false); // turn off automatic round robin if pinging
                                    // single sensor
        
        // do the ping to start getting a single range
        m_pingChannel.pulse(m_pingChannel.getChannelForRouting(), (float) m_pingTime); 
    }
    
    /**
     * Check if there is a valid range measurement. The ranges are accumulated
     * in a counter that will increment on each edge of the echo (return)
     * signal. If the count is not at least 2, then the range has not yet been
     * measured, and is invalid.
     *
     * @return true if the range is valid
     */
    public abstract boolean isRangeValid();
    
    protected boolean isRangeValid(double value)
    {

        double inches = value * m_conversionToInches;
        return inches < m_maxRange && inches > m_minRange;
    }

    /**
     * Get the range in inches from the ultrasonic sensor.
     *
     * @return double Range in inches of the target returned from the ultrasonic
     *         sensor. If there is no valid value yet, i.e. at least one
     *         measurement hasn't completed, then return 0.
     */
    public abstract double getRangeInches();

    /**
     * Get the range in millimeters from the ultrasonic sensor.
     *
     * @return double Range in millimeters of the target returned by the
     *         ultrasonic sensor. If there is no valid value yet, i.e. at least
     *         one measurement hasn't complted, then return 0.
     */
    public double getRangeMM()
    {
        return getRangeInches() * kmmPerIn;
    }
    
    public double getRangeInchesAverage()
    {
        if(m_automaticEnabled)
        {
            if (isRangeValid())
            {
                m_currentAverageDistance = 0;

                for (int i = 0; i < m_distanceBufferLength; i++)
                {
                    m_currentAverageDistance += m_distanceBufferInches[i];
                }

                m_currentAverageDistance /= m_distanceBufferLength;
            }
        }
        else
        {
            m_currentAverageDistance = getRangeInches();
        }

        return m_currentAverageDistance;
    }
    
    public double getRangeMMAverage()
    {
        return getRangeInchesAverage() * kmmPerIn;
    }

    /**
     * Get the range in the current DistanceUnit for the PIDSource base object.
     *
     * @return The range in DistanceUnit
     */
    public double pidGet() {
        switch (m_units.value) {
        case Unit.kInches_val:
            return getRangeInches();
        case Unit.kMillimeters_val:
            return getRangeMM();
        default:
            return 0.0;
        }
    }

    /**
     * Set the current DistanceUnit that should be used for the PIDSource base
     * object.
     *
     * @param units
     *            The DistanceUnit that should be used.
     */
    public void setDistanceUnits(Unit units) {
        m_units = units;
    }

    /**
     * Get the current DistanceUnit that is used for the PIDSource base object.
     *
     * @return The type of DistanceUnit that is being used.
     */
    public Unit getDistanceUnits() {
        return m_units;
    }

    /**
     * Is the ultrasonic enabled
     *
     * @return true if the ultrasonic is enabled
     */
    public boolean isEnabled() {
        return m_enabled;
    }

    /**
     * Set if the ultrasonic is enabled
     *
     * @param enable
     *            set to true to enable the ultrasonic
     */
    public void setEnabled(boolean enable) {
        m_enabled = enable;
    }

    public String getSmartDashboardType() {
        return "Ultrasonic";
    }

    private ITable m_table;

    public void initTable(ITable subtable) {
        m_table = subtable;
        updateTable();
    }

    public ITable getTable() {
        return m_table;
    }

    public void updateTable() {
        if (m_table != null) {
            m_table.putNumber("Value", getRangeInches());
        }
    }
    
    public void startLiveWindowMode() {
    }
    
    public void stopLiveWindowMode() {
    }
}
