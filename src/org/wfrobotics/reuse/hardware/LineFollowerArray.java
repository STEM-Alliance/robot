package org.wfrobotics.reuse.hardware;

import java.nio.ByteBuffer;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

/**
 * Class for the Line Follower Array 
 * Adapted from https://github.com/sparkfun/SparkFun_Line_Follower_Array_Arduino_Library
 * 
 */
public class LineFollowerArray
{
    protected static class Registers
    {
        public static final int REG_INPUT_DISABLE_B     = 0x00;   //  RegInputDisableB Input buffer disable register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_INPUT_DISABLE_A     = 0x01;   //  RegInputDisableA Input buffer disable register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_LONG_SLEW_B         = 0x02;   //  RegLongSlewB Output buffer long slew register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_LONG_SLEW_A         = 0x03;   //  RegLongSlewA Output buffer long slew register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_LOW_DRIVE_B         = 0x04;   //  RegLowDriveB Output buffer low drive register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_LOW_DRIVE_A         = 0x05;   //  RegLowDriveA Output buffer low drive register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_PULL_UP_B           = 0x06;   //  RegPullUpB Pull_up register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_PULL_UP_A           = 0x07;   //  RegPullUpA Pull_up register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_PULL_DOWN_B         = 0x08;   //  RegPullDownB Pull_down register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_PULL_DOWN_A         = 0x09;   //  RegPullDownA Pull_down register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_OPEN_DRAIN_B        = 0x0A;   //  RegOpenDrainB Open drain register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_OPEN_DRAIN_A        = 0x0B;   //  RegOpenDrainA Open drain register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_POLARITY_B          = 0x0C;   //  RegPolarityB Polarity register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_POLARITY_A          = 0x0D;   //  RegPolarityA Polarity register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_DIR_B               = 0x0E;   //  RegDirB Direction register _ I/O[15_8] (Bank B) 1111 1111
        public static final int REG_DIR_A               = 0x0F;   //  RegDirA Direction register _ I/O[7_0] (Bank A) 1111 1111
        public static final int REG_DATA_B              = 0x10;   //  RegDataB Data register _ I/O[15_8] (Bank B) 1111 1111*
        public static final int REG_DATA_A              = 0x11;   //  RegDataA Data register _ I/O[7_0] (Bank A) 1111 1111*
        public static final int REG_INTERRUPT_MASK_B    = 0x12;   //  RegInterruptMaskB Interrupt mask register _ I/O[15_8] (Bank B) 1111 1111
        public static final int REG_INTERRUPT_MASK_A    = 0x13;   //  RegInterruptMaskA Interrupt mask register _ I/O[7_0] (Bank A) 1111 1111
        public static final int REG_SENSE_HIGH_B        = 0x14;   //  RegSenseHighB Sense register for I/O[15:12] 0000 0000
        public static final int REG_SENSE_LOW_B         = 0x15;   //  RegSenseLowB Sense register for I/O[11:8] 0000 0000
        public static final int REG_SENSE_HIGH_A        = 0x16;   //  RegSenseHighA Sense register for I/O[7:4] 0000 0000
        public static final int REG_SENSE_LOW_A         = 0x17;   //  RegSenseLowA Sense register for I/O[3:0] 0000 0000
        public static final int REG_INTERRUPT_SOURCE_B  = 0x18;   //  RegInterruptSourceB Interrupt source register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_INTERRUPT_SOURCE_A  = 0x19;   //  RegInterruptSourceA Interrupt source register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_EVENT_STATUS_B      = 0x1A;   //  RegEventStatusB Event status register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_EVENT_STATUS_A      = 0x1B;   //  RegEventStatusA Event status register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_LEVEL_SHIFTER_1     = 0x1C;   //  RegLevelShifter1 Level shifter register 0000 0000
        public static final int REG_LEVEL_SHIFTER_2     = 0x1D;   //  RegLevelShifter2 Level shifter register 0000 0000
        public static final int REG_CLOCK               = 0x1E;   //  RegClock Clock management register 0000 0000
        public static final int REG_MISC                = 0x1F;   //  RegMisc Miscellaneous device settings register 0000 0000
        public static final int REG_LED_DRIVER_ENABLE_B = 0x20;   //  RegLEDDriverEnableB LED driver enable register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_LED_DRIVER_ENABLE_A = 0x21;   //  RegLEDDriverEnableA LED driver enable register _ I/O[7_0] (Bank A) 0000 0000
        
        // Debounce and Keypad Engine       
        public static final int REG_DEBOUNCE_CONFIG     = 0x22;    //  RegDebounceConfig Debounce configuration register 0000 0000
        public static final int REG_DEBOUNCE_ENABLE_B   = 0x23;    //  RegDebounceEnableB Debounce enable register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_DEBOUNCE_ENABLE_A   = 0x24;    //  RegDebounceEnableA Debounce enable register _ I/O[7_0] (Bank A) 0000 0000
        public static final int REG_KEY_CONFIG_1        = 0x25;    //  RegKeyConfig1 Key scan configuration register 0000 0000
        public static final int REG_KEY_CONFIG_2        = 0x26;    //  RegKeyConfig2 Key scan configuration register 0000 0000
        public static final int REG_KEY_DATA_1          = 0x27;    //  RegKeyData1 Key value (column) 1111 1111
        public static final int REG_KEY_DATA_2          = 0x28;    //  RegKeyData2 Key value (row) 1111 1111
        
        // LED Driver (PWM, blinking, breathing)
        public static final int REG_T_ON_0              = 0x29;    //  RegTOn0 ON time register for I/O[0] 0000 0000
        public static final int REG_I_ON_0              = 0x2A;    //  RegIOn0 ON intensity register for I/O[0] 1111 1111
        public static final int REG_OFF_0               = 0x2B;    //  RegOff0 OFF time/intensity register for I/O[0] 0000 0000
        public static final int REG_T_ON_1              = 0x2C;    //  RegTOn1 ON time register for I/O[1] 0000 0000
        public static final int REG_I_ON_1              = 0x2D;    //  RegIOn1 ON intensity register for I/O[1] 1111 1111
        public static final int REG_OFF_1               = 0x2E;    //  RegOff1 OFF time/intensity register for I/O[1] 0000 0000
        public static final int REG_T_ON_2              = 0x2F;    //  RegTOn2 ON time register for I/O[2] 0000 0000
        public static final int REG_I_ON_2              = 0x30;    //  RegIOn2 ON intensity register for I/O[2] 1111 1111
        public static final int REG_OFF_2               = 0x31;    //  RegOff2 OFF time/intensity register for I/O[2] 0000 0000
        public static final int REG_T_ON_3              = 0x32;    //  RegTOn3 ON time register for I/O[3] 0000 0000
        public static final int REG_I_ON_3              = 0x33;    //  RegIOn3 ON intensity register for I/O[3] 1111 1111
        public static final int REG_OFF_3               = 0x34;    //  RegOff3 OFF time/intensity register for I/O[3] 0000 0000
        public static final int REG_T_ON_4              = 0x35;    //  RegTOn4 ON time register for I/O[4] 0000 0000
        public static final int REG_I_ON_4              = 0x36;    //  RegIOn4 ON intensity register for I/O[4] 1111 1111
        public static final int REG_OFF_4               = 0x37;    //  RegOff4 OFF time/intensity register for I/O[4] 0000 0000
        public static final int REG_T_RISE_4            = 0x38;    //  RegTRise4 Fade in register for I/O[4] 0000 0000
        public static final int REG_T_FALL_4            = 0x39;    //  RegTFall4 Fade out register for I/O[4] 0000 0000
        public static final int REG_T_ON_5              = 0x3A;    //  RegTOn5 ON time register for I/O[5] 0000 0000
        public static final int REG_I_ON_5              = 0x3B;    //  RegIOn5 ON intensity register for I/O[5] 1111 1111
        public static final int REG_OFF_5               = 0x3C;    //  RegOff5 OFF time/intensity register for I/O[5] 0000 0000
        public static final int REG_T_RISE_5            = 0x3D;    //  RegTRise5 Fade in register for I/O[5] 0000 0000
        public static final int REG_T_FALL_5            = 0x3E;    //  RegTFall5 Fade out register for I/O[5] 0000 0000
        public static final int REG_T_ON_6              = 0x3F;    //  RegTOn6 ON time register for I/O[6] 0000 0000
        public static final int REG_I_ON_6              = 0x40;    //  RegIOn6 ON intensity register for I/O[6] 1111 1111
        public static final int REG_OFF_6               = 0x41;    //  RegOff6 OFF time/intensity register for I/O[6] 0000 0000
        public static final int REG_T_RISE_6            = 0x42;    //  RegTRise6 Fade in register for I/O[6] 0000 0000
        public static final int REG_T_FALL_6            = 0x43;    //  RegTFall6 Fade out register for I/O[6] 0000 0000
        public static final int REG_T_ON_7              = 0x44;    //  RegTOn7 ON time register for I/O[7] 0000 0000
        public static final int REG_I_ON_7              = 0x45;    //  RegIOn7 ON intensity register for I/O[7] 1111 1111
        public static final int REG_OFF_7               = 0x46;    //  RegOff7 OFF time/intensity register for I/O[7] 0000 0000
        public static final int REG_T_RISE_7            = 0x47;    //  RegTRise7 Fade in register for I/O[7] 0000 0000
        public static final int REG_T_FALL_7            = 0x48;    //  RegTFall7 Fade out register for I/O[7] 0000 0000
        public static final int REG_T_ON_8              = 0x49;    //  RegTOn8 ON time register for I/O[8] 0000 0000
        public static final int REG_I_ON_8              = 0x4A;    //  RegIOn8 ON intensity register for I/O[8] 1111 1111
        public static final int REG_OFF_8               = 0x4B;    //  RegOff8 OFF time/intensity register for I/O[8] 0000 0000
        public static final int REG_T_ON_9              = 0x4C;    //  RegTOn9 ON time register for I/O[9] 0000 0000
        public static final int REG_I_ON_9              = 0x4D;    //  RegIOn9 ON intensity register for I/O[9] 1111 1111
        public static final int REG_OFF_9               = 0x4E;    //  RegOff9 OFF time/intensity register for I/O[9] 0000 0000
        public static final int REG_T_ON_10             = 0x4F;    //  RegTOn10 ON time register for I/O[10] 0000 0000
        public static final int REG_I_ON_10             = 0x50;    //  RegIOn10 ON intensity register for I/O[10] 1111 1111
        public static final int REG_OFF_10              = 0x51;    //  RegOff10 OFF time/intensity register for I/O[10] 0000 0000
        public static final int REG_T_ON_11             = 0x52;    //  RegTOn11 ON time register for I/O[11] 0000 0000
        public static final int REG_I_ON_11             = 0x53;    //  RegIOn11 ON intensity register for I/O[11] 1111 1111
        public static final int REG_OFF_11              = 0x54;    //  RegOff11 OFF time/intensity register for I/O[11] 0000 0000
        public static final int REG_T_ON_12             = 0x55;    //  RegTOn12 ON time register for I/O[12] 0000 0000
        public static final int REG_I_ON_12             = 0x56;    //  RegIOn12 ON intensity register for I/O[12] 1111 1111
        public static final int REG_OFF_12              = 0x57;    //  RegOff12 OFF time/intensity register for I/O[12] 0000 0000
        public static final int REG_T_RISE_12           = 0x58;    //  RegTRise12 Fade in register for I/O[12] 0000 0000
        public static final int REG_T_FALL_12           = 0x59;    //  RegTFall12 Fade out register for I/O[12] 0000 0000
        public static final int REG_T_ON_13             = 0x5A;    //  RegTOn13 ON time register for I/O[13] 0000 0000
        public static final int REG_I_ON_13             = 0x5B;    //  RegIOn13 ON intensity register for I/O[13] 1111 1111
        public static final int REG_OFF_13              = 0x5C;    //  RegOff13 OFF time/intensity register for I/O[13] 0000 0000
        public static final int REG_T_RISE_13           = 0x5D;    //  RegTRise13 Fade in register for I/O[13] 0000 0000
        public static final int REG_T_FALL_13           = 0x5E;    //  RegTFall13 Fade out register for I/O[13] 0000 0000
        public static final int REG_T_ON_14             = 0x5F;    //  RegTOn14 ON time register for I/O[14] 0000 0000
        public static final int REG_I_ON_14             = 0x60;    //  RegIOn14 ON intensity register for I/O[14] 1111 1111
        public static final int REG_OFF_14              = 0x61;    //  RegOff14 OFF time/intensity register for I/O[14] 0000 0000
        public static final int REG_T_RISE_14           = 0x62;    //  RegTRise14 Fade in register for I/O[14] 0000 0000
        public static final int REG_T_FALL_14           = 0x63;    //  RegTFall14 Fade out register for I/O[14] 0000 0000
        public static final int REG_T_ON_15             = 0x64;    //  RegTOn15 ON time register for I/O[15] 0000 0000
        public static final int REG_I_ON_15             = 0x65;    //  RegIOn15 ON intensity register for I/O[15] 1111 1111
        public static final int REG_OFF_15              = 0x66;    //  RegOff15 OFF time/intensity register for I/O[15] 0000 0000
        public static final int REG_T_RISE_15           = 0x67;    //  RegTRise15 Fade in register for I/O[15] 0000 0000
        public static final int REG_T_FALL_15           = 0x68;    //  RegTFall15 Fade out register for I/O[15] 0000 0000
        
        //  Miscellaneous       
        public static final int REG_HIGH_INPUT_B        = 0x69;   //  RegHighInputB High input enable register _ I/O[15_8] (Bank B) 0000 0000
        public static final int REG_HIGH_INPUT_A        = 0x6A;   //  RegHighInputA High input enable register _ I/O[7_0] (Bank A) 0000 0000
        
        //  Software Reset      
        public static final int REG_RESET               = 0x7D;    //  RegReset Software reset register 0000 0000
        public static final int REG_TEST_1              = 0x7E;    //  RegTest1 Test register 0000 0000
        public static final int REG_TEST_2              = 0x7F;    //  RegTest2 Test register 0000 0000
        
        public static final int DEFAULT_VALUE           = 0xFF;
        
        public static final int REG_I_ON[] = {  REG_I_ON_0, REG_I_ON_1, REG_I_ON_2, REG_I_ON_3,
                                                REG_I_ON_4, REG_I_ON_5, REG_I_ON_6, REG_I_ON_7,
                                                REG_I_ON_8, REG_I_ON_9, REG_I_ON_10, REG_I_ON_11,
                                                REG_I_ON_12, REG_I_ON_13, REG_I_ON_14, REG_I_ON_15};
                            
        public static final int REG_T_ON[] = {  REG_T_ON_0, REG_T_ON_1, REG_T_ON_2, REG_T_ON_3,
                                                REG_T_ON_4, REG_T_ON_5, REG_T_ON_6, REG_T_ON_7,
                                                REG_T_ON_8, REG_T_ON_9, REG_T_ON_10, REG_T_ON_11,
                                                REG_T_ON_12, REG_T_ON_13, REG_T_ON_14, REG_T_ON_15};
                            
        public static final int REG_OFF[] = {   REG_OFF_0, REG_OFF_1, REG_OFF_2, REG_OFF_3,
                                                REG_OFF_4, REG_OFF_5, REG_OFF_6, REG_OFF_7,
                                                REG_OFF_8, REG_OFF_9, REG_OFF_10, REG_OFF_11,
                                                REG_OFF_12, REG_OFF_13, REG_OFF_14, REG_OFF_15};
        
        public static final int REG_T_RISE[] = {0xFF, 0xFF, 0xFF, 0xFF,
                                                REG_T_RISE_4, REG_T_RISE_5, REG_T_RISE_6, REG_T_RISE_7,
                                                0xFF, 0xFF, 0xFF, 0xFF,
                                                REG_T_RISE_12, REG_T_RISE_13, REG_T_RISE_14, REG_T_RISE_15};
                            
        public static final int REG_T_FALL[] = {0xFF, 0xFF, 0xFF, 0xFF,
                                                REG_T_FALL_4, REG_T_FALL_5, REG_T_FALL_6, REG_T_FALL_7,
                                                0xFF, 0xFF, 0xFF, 0xFF,
                                                REG_T_FALL_12, REG_T_FALL_13, REG_T_FALL_14, REG_T_FALL_15};
    }

    // all possible I2C addresses
    // RoboRio sometimes does dumb stuff with this,
    // so try forcing the MSB high if needed
    public static enum DEVICE {
        DEVICE_0(0x3E), DEVICE_1(0x3F), DEVICE_2(0x70), DEVICE_3(0x71);

        private final int value;

        private DEVICE(int value)
        {
            this.value = value;
        }

        public int get()
        {
            return value;
        }
    }

    private static final double STROBE_DELAY_TIME_S = 0.0001;

    private I2C m_i2c;

    private int m_lastBarRawValue;
    private int m_lastBarPositionValue;

    private boolean m_strobe; // 0 = always on, 1 = on only when reading
    private boolean m_invert; // 1 = invert

    /**
     * 
     * @param port
     * @param device
     */
    public LineFollowerArray(I2C.Port port, DEVICE device)
    {
        this(port, device, false, false);
    }

    /**
     * 
     * @param port
     * @param device
     * @param strobe
     * @param invert
     */
    public LineFollowerArray(I2C.Port port, DEVICE device, boolean strobe, boolean invert)
    {
        m_i2c = new I2C(port, device.get());

        m_strobe = strobe;
        m_invert = invert;

        // reset the device
        reset();

        // test the registers first to make sure we can talk to it
        int reg = readWord(Registers.REG_INTERRUPT_MASK_A);
        if (reg != 0xff00)
        {
            DriverStation.reportError("LineFollower device "
                                      + device.get()
                                      + " failed to initialize", false);
        }
    }

    int getRaw()
    {
        // Get the information from the wire, stores in lastBarRawValue
        scan();

        return m_lastBarRawValue;
    }

    int getPosition()
    {
        // Assign values to each bit, -127 to 127, sum, and divide
        int accumulator = 0;
        int bitsCounted = 0;
        int i;

        // Get the information from the wire, stores in lastBarRawValue
        scan();

        // count bits
        for (i = 0; i < 8; i++)
        {
            if (((m_lastBarRawValue >> i) & 0x01) == 1)
            {
                bitsCounted++;
            }
        }

        // Find the vector value of each positive bit and sum
        for (i = 7; i > 3; i--) // iterate negative side bits
        {
            if (((m_lastBarRawValue >> i) & 0x01) == 1)
            {
                accumulator += ((-32 * (i - 3)) + 1);
            }
        }
        for (i = 0; i < 4; i++) // iterate positive side bits
        {
            if (((m_lastBarRawValue >> i) & 0x01) == 1)
            {
                accumulator += ((32 * (4 - i)) - 1);
            }
        }

        if (bitsCounted > 0)
        {
            m_lastBarPositionValue = accumulator / bitsCounted;
        }
        else
        {
            m_lastBarPositionValue = 0;
        }

        return m_lastBarPositionValue;
    }

    int getDensity()
    {
        int bitsCounted = 0;
        int i;

        // get input from the I2C machine
        scan();

        // count bits
        for (i = 0; i < 8; i++)
        {
            if (((m_lastBarRawValue >> i) & 0x01) == 1)
            {
                bitsCounted++;
            }
        }
        return bitsCounted;
    }

    /**
     * true will strobe LEDs, false is always on
     * 
     * @param strobe
     */
    void setStrobe(boolean strobe)
    {
        m_strobe = strobe;
    }

    /**
     * true is light line on dark, false is dark line on light
     * 
     * @param invert
     */
    void invert(boolean invert)
    {
        m_invert = invert;
    }

    /**
     * reset the device
     */
    void reset()
    {
        writeByte(Registers.REG_RESET, 0x12);
        writeByte(Registers.REG_RESET, 0x34);
    }

    /**
     * turn off LEDs and sensors
     */
    void disable()
    {
        writeByte(Registers.REG_DATA_B, 0x03);
    }
    
    /**
     * perform a scan
     */
    private void scan()
    {
        if (m_strobe)
        {
            // Turn on LEDs
            writeByte(Registers.REG_DATA_B, 0x02);

            Timer.delay(STROBE_DELAY_TIME_S);

            // Turn on sensors
            writeByte(Registers.REG_DATA_B, 0x00);
        }
        else
        {
            // make sure both IR and indicators are on
            writeByte(Registers.REG_DATA_B, 0x00);
        }

        // get the bar values from port A
        m_lastBarRawValue = readByte(Registers.REG_DATA_A);

        if (m_invert)
        {
            // Invert the bits if needed
            m_lastBarRawValue ^= 0xFF;
        }

        if (m_strobe)
        {
            // Turn off LEDs and sensors when done
            writeByte(Registers.REG_DATA_B, 0x03);
        }

    }

    private byte readByte(int reg)
    {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1);

        if (!m_i2c.read(reg, 1, buffer))
        {
            return buffer.get();
        }
        else
        {
            return 0;
        }
    }

    private int readWord(int reg)
    {
        ByteBuffer buffer = ByteBuffer.allocateDirect(2);

        if (!m_i2c.read(reg, 2, buffer))
        {
            return buffer.getShort();
        }
        else
        {
            return 0;
        }
    }

    // private int[] readBytes(int reg, int length)
    // {
    // ByteBuffer buffer = ByteBuffer.allocateDirect(length);
    //
    // i2c.read(reg, length, buffer);
    //
    // return buffer.get();
    // }

    /**
     * 
     * @param reg
     * @param writeValue
     */
    private void writeByte(int reg, int writeValue)
    {
        m_i2c.write(reg, writeValue);
    }

    /**
     * 
     * @param reg
     * @param writeValue
     *            16bit value to write
     */
    private void writeWord(int reg, int writeValue)
    {
        byte[] buffer = new byte[3];
        buffer[0] = (byte) reg;
        buffer[1] = (byte) (writeValue >> 8);
        buffer[2] = (byte) (writeValue & 0xff);

        m_i2c.writeBulk(buffer);
    }

    /**
     * 
     * @param reg
     * @param writeArray
     *            8bit array of values
     * @param length
     */
    private void writeBytes(int reg, int[] writeArray, int length)
    {
        byte[] buffer = new byte[length + 1];

        buffer[0] = (byte) reg;

        for (int i = 0; i < length; i++)
        {
            buffer[i + 1] = (byte) writeArray[i];
        }

        m_i2c.writeBulk(buffer);
    }
}
