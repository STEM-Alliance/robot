package org.wfrobotics.reuse.hardware;

import java.nio.ByteBuffer;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

/**
 * Class 1 laser rangefinder class for the LIDARLite v3 from Garmin. 
 * Communication is over I2C, using registers from the datasheet.
 * If using more than one device, changing the address is required, and a
 * digital pin is needed for enabling power.
 * @see <a href="http://static.garmin.com/pumac/LIDAR_Lite_v3_Operation_Manual_and_Technical_Specifications.pdf">Manual</a>
 * @see <a href="https://www.sparkfun.com/products/14032">Sparkfun</a>
 * @see <a href="https://github.com/PulsedLight3D/LIDARLite_v2_Arduino_Library/blob/master/LIDARLite/LIDARLite.cpp">Arduino Library</a>
 */
public class LIDAR {

    public enum Configuration
    {
        /**
         * Default configuration
         */
        DEFAULT,
        /**
         * Set acquisition count to 1/3 default value, faster reads,
         * slightly noisier values
         */
        HIGH_SPEED_HIGH_NOISE,
        /**
         * Low noise, low sensitivity: Pulls decision criteria higher 
         * above the noise, allows fewer false detections, reduces sensitivity
         */
        LOW_NOISE_LOW_SENSITIVITY,
        /**
         * High noise, high sensitivity: Pulls decision criteria into the
         * noise, allows more false detections, increases sensitivity
         */
        HIGH_NOISE_HIGH_SENSITIVITY
    };

    @SuppressWarnings("unused")
    private class StatusBit {
        /**
         * Bit 0: busy taking a measurement
         */
        public final static int BUSY = 0b1;
        
        /**
         * Bit 1: reference data in correlation record has reached it's max
         */
        public final static int REFERENCE_OVERFLOW = 0b10;
        
        /**
         * Bit 2: signal data in correlation record has reached it's max
         */
        public final static int SIGNAL_OVERFLOW = 0b100;
        
        /**
         * Bit 3: peak not detected in correlation record, measurement is invalid
         */
        public final static int INVALID_SIGNAL = 0b1000;
        
        /**
         * Bit 4: secondary return detected in correlation record
         */
        public final static int SEONDARY_RETURN = 0b10000;
        
        /**
         * Bit 5: reference and receiver bias are operational
         */
        public final static int HEALTH = 0b10000;
        
        /**
         * Bit 6: system error detected during measurement
         */
        public final static int PROCESS_ERROR = 0b100000;
    }

    @SuppressWarnings("unused")
    public class Registers
    {
        private final static int ACQ_COMMAND          = 0x00;
        private final static int STATUS               = 0x01;
        private final static int SIG_COUNT_VAL        = 0x02;
        private final static int ACQ_CONFIG_REG       = 0x04;
        private final static int VELOCITY             = 0x09;
        private final static int PEAK_CORR            = 0x0c;
        private final static int NOISE_PEAK           = 0x0d;
        private final static int SIGNAL_STRENGTH      = 0x0e;
        private final static int FULL_DIST_HIGH       = 0x0f;
        private final static int FULL_DIST_LOW        = 0x10;
        private final static int OUTER_LOOP_COUNT     = 0x11;
        private final static int REF_COUNT_VAL        = 0x12;
        private final static int LAST_DIST_HIGH       = 0x14;
        private final static int LAST_DIST_LOW        = 0x15;
        private final static int UNIT_ID_HIGH         = 0x16;
        private final static int UNIT_ID_LOW          = 0x17;
        private final static int I2C_ID_HIGH          = 0x18;
        private final static int I2C_ID_LOW           = 0x19;
        private final static int I2C_SEC_ADDR         = 0x1a;
        private final static int THRESHOLD_BYPASS     = 0x1c;
        private final static int I2C_CONFIG           = 0x1e;
        private final static int COMMAND              = 0x40;
        private final static int MEASURE_DELAY        = 0x45;
        private final static int PEAK_BCK             = 0x4c;
        private final static int CORR_DATA            = 0x52;
        private final static int CORR_DATA_SIGN       = 0x53;
        private final static int ACQ_SETTINGS         = 0x5d;
        private final static int POWER_CONTROL        = 0x65;
        
        // if you want to read consecutive registers, OR with this mask
        private final static int AUTO_INC_MASK        = 0x80;
    }
    
    private I2C i2c;
    private int lastDistanceInCm;
    
    @SuppressWarnings("unused")
    private DigitalOutput powerEnable;

    private final static int LIDAR_ADDR           = 0x62;
    private final static double IDLE_TIMEOUT_S    = .05;
    
    /**
     * Initialize the LIDAR
     * @param port on Rio or MXP
     * @param config base configuration mode
     */
    public LIDAR(I2C.Port port, Configuration config)
    {
        this(port, config, LIDAR_ADDR);
    }
    
    /**
     * Initialize the LIDAR
     * @param port on Rio or MXP
     * @param config base configuration mode
     * @param address custom I2C device address
     * @param enablePin power enable DIO pin
     */
    public LIDAR(I2C.Port port, Configuration config, int address, int enablePin)
    {
        this(port, config, address);
        
        powerEnable = new DigitalOutput(enablePin);
    }

    /**
     * Initialize the LIDAR
     * @param port on Rio or MXP
     * @param config base configuration mode
     * @param address custom I2C device address
     */
    public LIDAR(I2C.Port port, Configuration config, int address)
    {
        i2c = new I2C(port, address);
        
        // reset
        i2c.write(Registers.ACQ_COMMAND,0x00);
        // then configure
        configure(config);
        
    }
    
    /**
     * Set the base configuration
     * @param config
     */
    private void configure(Configuration config)
    {
        switch (config)
        {
            case DEFAULT:
                i2c.write(Registers.ACQ_COMMAND,0x00);
                break;
                
            case HIGH_SPEED_HIGH_NOISE: 
                i2c.write(Registers.ACQ_CONFIG_REG,0x00);
                break;
                
            case LOW_NOISE_LOW_SENSITIVITY:
                i2c.write(Registers.THRESHOLD_BYPASS,0x20);
                break;
                
            case HIGH_NOISE_HIGH_SENSITIVITY:
                i2c.write(Registers.THRESHOLD_BYPASS,0x60);
                break;
          }
    }
    
    /**
     * 
     * @param modePinLow if true the Mode pin will pull low when a new measurement is available
     * @param interval set the time between measurements
     * @param numberOfReadings sets the number of readings to take before stopping 
     *      (Note: even though the sensor will stop taking new readings, 0x8f will
     *      still read back the last recorded value). 0xff will take infinite readings
     *      without stopping. Minimum value for operation is 0x02.
     */
    public void beginContinuous(boolean modePinLow, byte interval, byte numberOfReadings)
    {
        //  Register 0x45 sets the time between measurements. 0xc8 corresponds to 10Hz
        //  while 0x13 corresponds to 100Hz. Minimum value is 0x02 for proper
        //  operation.
        i2c.write(Registers.MEASURE_DELAY, interval);
        
        //  Set register 0x04 to 0x20 to look at "NON-default" value of velocity scale
        //  If you set bit 0 of 0x04 to "1" then the mode pin will be low when done
        if(modePinLow)
        {
            i2c.write(Registers.ACQ_CONFIG_REG, 0x21);
        }
        else
        {
            i2c.write(Registers.ACQ_CONFIG_REG, 0x20);
        }
        
        //  Set the number of readings, 0xfe = 254 readings, 0x01 = 1 reading and
        //  0xff = continuous readings
        i2c.write(Registers.OUTER_LOOP_COUNT, numberOfReadings);
        
        //  Initiate reading distance
        i2c.write(Registers.ACQ_COMMAND, 0x04);
    }

    /**
     * Get the latest distance, will request a new sample
     * @return distance in cm
     */
    public int getDistanceInCm()
    {
        return getDistanceInCm(true, false);
    }
    
    /**
     * Get the latest distance
     * @param startMeasurement if true, a measurement request is needed
     * @param stabilizePreampFlag if true, take acquisition with DC stabilization/correction.
     * @return distance in cm
     */
    public int getDistanceInCm(boolean startMeasurement, boolean stabilizePreampFlag)
    {
        return getDistanceInCm(startMeasurement, stabilizePreampFlag, true);
    }
    
    /**
     * Get the latest distance
     * @param startMeasurement if true, a measurement request is needed
     * @param stabilizePreampFlag if true, take acquisition with DC stabilization/correction.
     * @param busyWait if true, wait for the sensor to not be busy before reading.
     * @return distance in cm
     */
    public int getDistanceInCm(boolean startMeasurement, boolean stabilizePreampFlag, boolean busyWait)
    {
        ByteBuffer distance = ByteBuffer.allocate(2);
        
        if(startMeasurement)
        {
            startMeasurement(stabilizePreampFlag);
        }
        
        // get measurement while waiting for idle
        read(Registers.AUTO_INC_MASK | Registers.FULL_DIST_HIGH, 2, distance, busyWait);
        
        lastDistanceInCm = distance.getShort();
        
        return lastDistanceInCm;
    }
    
    /**
     * Get the latest distance. NOTE: requires DIO pin wired to sensor's MODE signal
     * @return distance in cm
     */
    public int getDistanceContinuousInCm()
    {
        return getDistanceInCm(false, false, false);
    }
    
    /**
     * Request a new measurement to start, with DC stabilization/correction
     */
    public void startMeasurement()
    {
        startMeasurement(true);
    }
    
    /**
     * Request a new measurement to start
     * @param stablizePreampFlag if true, take acquisition with DC stabilization/correction.
     *                            if false, it will read faster, but you will need to stabilize
     *                            DC every once in awhile (ex. 1 out of every 100 readings
     *                            is typically good).
     */
    public void startMeasurement(boolean stablizePreampFlag)
    {
        if(stablizePreampFlag)
        {
            // Take acquisition & correlation processing with DC correction
            i2c.write(Registers.ACQ_COMMAND, 0x04);
        }
        else
        {
            // Take acquisition & correlation processing without DC correction
            i2c.write(Registers.ACQ_COMMAND, 0x03);
        }
    }
    
    /**
     * Read from register(s)
     * @param reg register to start read from
     * @param size number of registers to read
     * @param result {@link ByteBuffer} used to store result
     * @param busyWait if true, wait until it is not busy
     * @return true if successful
     */
    public boolean read(int reg, int size, ByteBuffer result, boolean busyWait)
    {
        boolean success = false;
        boolean idle = true;
        
        if(busyWait)
        {
            idle = waitForIdle();
        }
        
        if(idle)
        {
            success = !i2c.read(reg, size, result);
        }
        
        return success;
    }
    
    
    /**
     * Blocking call to wait for an idle
     * @return true if idle without faults, false if faults exist or timed out
     */
    private boolean waitForIdle()
    {
        ByteBuffer status = ByteBuffer.allocate(1);
        int val = 0;
        double startTime = Timer.getFPGATimestamp();
        
        
        // wait while the status is busy
        // exit if done or if any fault exists
        do
        {
            i2c.read(Registers.STATUS, 1, status);
            val = status.get();
            
            //TODO: Are these the only errors we care about?
            if((val & StatusBit.PROCESS_ERROR) != 0)
            {
                return false;
            }
            else if ((val & StatusBit.INVALID_SIGNAL) != 0)
            {
                return false;
            }
            
            if((Timer.getFPGATimestamp() - startTime) > IDLE_TIMEOUT_S)
            {
                return false;
            }
            
        } while ( (val & StatusBit.BUSY) != 0x00 );
        
        return true;
    }

}
