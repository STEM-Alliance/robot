package com.taurus.robotspecific2015;

import com.taurus.led.Color;
import com.taurus.led.Hardware;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * {@inheritDoc}
 */
public class LEDController extends Hardware{
    
    private SPI spi;
    private DigitalOutput latch;
    
    public static final int NumOfColors = 4;
    
    public LEDController() {
        init();
    }
    
    /**
     * {@inheritDoc}
     */
    public void init()
    {
        // Hardware
        spi = new SPI(Port.kOnboardCS0);
        spi.setClockRate(3000000);
        spi.setMSBFirst();
        latch = new DigitalOutput(Constants.LED_LATCH);
    }
    
    /**
     * {@inheritDoc}
     */
    public void update(Color[] colors)
    {
        byte[] out = new byte[(int) (NumOfColors * 4.5)];
        
        for (int i = 0; i < NumOfColors; i++)
        {
            colors[i].Get(i % 2 == 0 ? false : true, out, (int) (i * 4.5));
        }
        
        String text = byteArrayToHex(out);
        SmartDashboard.putString("LEDs", text);

        latch.set(false);
        Timer.delay(.001);

        spi.write(out, out.length);

        latch.set(true);
        Timer.delay(.001);
        latch.set(false);
    }
    
    private static String byteArrayToHex(byte[] a)
    {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
