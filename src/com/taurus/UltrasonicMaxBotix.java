package com.taurus;


import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;


/**
 * Ultrasonic rangefinder class for MaxBotix sensors. Uses the built in 
 * Ultrasonic class, but auto converts the distances based on the datasheet
 * for MaxBotix sensors.
 */
public class UltrasonicMaxBotix extends SensorBase implements PIDSource, LiveWindowSendable {


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

        private static final double kPingTime = 10 * 1e-6; // /< Time (sec) for the
                                                            // ping trigger pulse.
        private static final int kPriority = 90; // /< Priority that the ultrasonic
                                                    // round robin task runs.
        private static final double kMaxUltrasonicTime = 0.05; // /< Max time (ms)
                                                                // between readings.
        private static final double kSpeedOfSoundInchesPerSec = 1130.0 * 12.0;
        private static UltrasonicMaxBotix m_firstSensor = null; // head of the ultrasonic
                                                        // sensor list
        private static boolean m_automaticEnabled = false; // automatic round robin
                                                            // mode
        private DigitalInput m_echoChannel = null;
        private DigitalOutput m_pingChannel = null;
        private boolean m_allocatedChannels;
        private boolean m_enabled = false;
        private Counter m_counter = null;
        private double m_oldDistance = 0;
        private UltrasonicMaxBotix m_nextSensor = null;
        private static Thread m_task = null; // task doing the round-robin automatic
                                                // sensing
        private Unit m_units;
        private static int m_instances = 0;
        
        // grabbed from Ultrasonic.class
        private static final double UltrasonicConstant = 1130.0 * 12.0 / 2.0;
        
        // grabbed from MaxBotix datasheet, 1uS == 1mm
        private double uS_Per_MM = 1.0;
        
        private static final double mmPerIn = 25.4;
        private static final double uS_Per_S = 1000000.0;
            
        /**
         * Manually set/change the number of uSeconds per MM
         * @param val
         */
        public void setUSPerMM(double val)
        {
            uS_Per_MM = val;
        }

        /**
         * Get the number of uSeconds per MM
         * @return
         */
        public double setUSPerMM()
        {
            return uS_Per_MM;
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
        private class UltrasonicChecker extends Thread {

            public synchronized void run() {
                UltrasonicMaxBotix u = null;
                while (m_automaticEnabled) {
                    if (u == null) {
                        u = m_firstSensor;
                    }
                    if (u == null) {
                        return;
                    }
                    if (u.isEnabled()) {
                        u.m_pingChannel.pulse(m_pingChannel.getChannelForRouting(),
                                (float) kPingTime); // do the ping
                    }
                    u = u.m_nextSensor;
                    Timer.delay(kMaxUltrasonicTime); // wait for ping to return
                }
            }
        }

        /**
         * Initialize the Ultrasonic Sensor. This is the common code that
         * initializes the ultrasonic sensor given that there are two digital I/O
         * channels allocated. If the system was running in automatic mode (round
         * robin) when the new sensor is added, it is stopped, the sensor is added,
         * then automatic mode is restored.
         */
        private synchronized void initialize() {
            if (m_task == null) {
                m_task = new UltrasonicChecker();
            }
            boolean originalMode = m_automaticEnabled;
            setAutomaticMode(false); // kill task when adding a new sensor
            m_nextSensor = m_firstSensor;
            m_firstSensor = this;

            m_counter = new Counter(m_echoChannel); // set up counter for this
                                                    // sensor
            m_counter.setMaxPeriod(1.0);
            m_counter.setSemiPeriodMode(true);
            m_counter.reset();
            m_enabled = true; // make it available for round robin scheduling
            setAutomaticMode(originalMode);

            m_instances++;
            UsageReporting.report(tResourceType.kResourceType_Ultrasonic,
                    m_instances);
            LiveWindow.addSensor("Ultrasonic", m_echoChannel.getChannel(), this);
        }

        /**
         * Create an instance of the Ultrasonic Sensor.
         * This is designed to supchannel the Daventech SRF04 and Vex ultrasonic
         * sensors.
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
        public UltrasonicMaxBotix(final int pingChannel, final int echoChannel, Unit units) {
            m_pingChannel = new DigitalOutput(pingChannel);
            m_echoChannel = new DigitalInput(echoChannel);
            m_allocatedChannels = true;
            m_units = units;
            initialize();
        }

        /**
         * Create an instance of the Ultrasonic Sensor.
         * This is designed to supchannel the Daventech SRF04 and Vex ultrasonic
         * sensors. Default unit is inches.
         *
         * @param pingChannel
         *            The digital output channel that sends the pulse to initiate
         *            the sensor sending the ping.
         * @param echoChannel
         *            The digital input channel that receives the echo. The length
         *            of time that the echo is high represents the round trip time
         *            of the ping, and the distance.
         */
        public UltrasonicMaxBotix(final int pingChannel, final int echoChannel) {
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
        public UltrasonicMaxBotix(DigitalOutput pingChannel, DigitalInput echoChannel,
                Unit units) {
            if (pingChannel == null || echoChannel == null) {
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
        public UltrasonicMaxBotix(DigitalOutput pingChannel, DigitalInput echoChannel) {
            this(pingChannel, echoChannel, Unit.kInches);
        }

        /**
         * Destructor for the ultrasonic sensor. Delete the instance of the
         * ultrasonic sensor by freeing the allocated digital channels. If the
         * system was in automatic mode (round robin), then it is stopped, then
         * started again after this sensor is removed (provided this wasn't the last
         * sensor).
         */
        public synchronized void free() {
            boolean wasAutomaticMode = m_automaticEnabled;
            setAutomaticMode(false);
            if (m_allocatedChannels) {
                if (m_pingChannel != null) {
                    m_pingChannel.free();
                }
                if (m_echoChannel != null) {
                    m_echoChannel.free();
                }
            }

            if (m_counter != null) {
                m_counter.free();
                m_counter = null;
            }

            m_pingChannel = null;
            m_echoChannel = null;

            if (this == m_firstSensor) {
                m_firstSensor = m_nextSensor;
                if (m_firstSensor == null) {
                    setAutomaticMode(false);
                }
            } else {
                for (UltrasonicMaxBotix s = m_firstSensor; s != null; s = s.m_nextSensor) {
                    if (this == s.m_nextSensor) {
                        s.m_nextSensor = s.m_nextSensor.m_nextSensor;
                        break;
                    }
                }
            }
            if (m_firstSensor != null && wasAutomaticMode) {
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
        public void setAutomaticMode(boolean enabling) {
            if (enabling == m_automaticEnabled) {
                return; // ignore the case of no change
            }
            m_automaticEnabled = enabling;

            if (enabling) {
                // enabling automatic mode.
                // Clear all the counters so no data is valid
                for (UltrasonicMaxBotix u = m_firstSensor; u != null; u = u.m_nextSensor) {
                    u.m_counter.reset();
                }
                // Start round robin task
                m_task.start();
            } else {
                // disabling automatic mode. Wait for background task to stop
                // running.
                while (m_task.isAlive()) {
                    Timer.delay(.15); // just a little longer than the ping time for
                                        // round-robin to stop
                }
                // clear all the counters (data now invalid) since automatic mode is
                // stopped
                for (UltrasonicMaxBotix u = m_firstSensor; u != null; u = u.m_nextSensor) {
                    u.m_counter.reset();
                }
            }
        }

        /**
         * Single ping to ultrasonic sensor. Send out a single ping to the
         * ultrasonic sensor. This only works if automatic (round robin) mode is
         * disabled. A single ping is sent out, and the counter should count the
         * semi-period when it comes in. The counter is reset to make the current
         * value invalid.
         */
        public void ping() {
            setAutomaticMode(false); // turn off automatic round robin if pinging
                                        // single sensor
            m_counter.reset(); // reset the counter to zero (invalid data now)
            m_pingChannel.pulse(m_pingChannel.getChannelForRouting(), (float) kPingTime); // do
                                                                                // the
                                                                                // ping
                                                                                // to
                                                                                // start
                                                                                // getting
                                                                                // a
                                                                                // single
                                                                                // range
        }

        /**
         * Check if there is a valid range measurement. The ranges are accumulated
         * in a counter that will increment on each edge of the echo (return)
         * signal. If the count is not at least 2, then the range has not yet been
         * measured, and is invalid.
         *
         * @return true if the range is valid
         */
        public boolean isRangeValid() {
            return m_counter.get() > 1 && m_counter.getPeriod() * uS_Per_S / uS_Per_MM / mmPerIn < 400;
        }

        /**
         * Get the range in inches from the ultrasonic sensor.
         *
         * @return double Range in inches of the target returned from the ultrasonic
         *         sensor. If there is no valid value yet, i.e. at least one
         *         measurement hasn't completed, then return 0.
         */
        public double getRangeInches() {
            if (isRangeValid()) {
                
                double currentInches = m_counter.getPeriod() * uS_Per_S / uS_Per_MM / mmPerIn;
                double weightedAvg = (m_oldDistance + currentInches)/2;
                m_oldDistance = currentInches;
                return weightedAvg;
                
            } else {
                return 0;
            }
        }

        /**
         * Get the range in millimeters from the ultrasonic sensor.
         *
         * @return double Range in millimeters of the target returned by the
         *         ultrasonic sensor. If there is no valid value yet, i.e. at least
         *         one measurement hasn't complted, then return 0.
         */
        public double getRangeMM() {
            return getRangeInches() * 25.4;
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

        /*
         * Live Window code, only does anything if live window is activated.
         */
        public String getSmartDashboardType() {
            return "Ultrasonic";
        }

        private ITable m_table;

        /**
         * {@inheritDoc}
         */
        public void initTable(ITable subtable) {
            m_table = subtable;
            updateTable();
        }

        /**
         * {@inheritDoc}
         */
        public ITable getTable() {
            return m_table;
        }

        /**
         * {@inheritDoc}
         */
        public void updateTable() {
            if (m_table != null) {
                m_table.putNumber("Value", getRangeInches());
            }
        }

        /**
         * {@inheritDoc}
         */
        public void startLiveWindowMode() {
        }

        /**
         * {@inheritDoc}
         */
        public void stopLiveWindowMode() {
        }
}
