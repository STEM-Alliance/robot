package com.taurus.robotspecific2015;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

public class LEDs implements Runnable {

    SPI spi;
    DigitalOutput latch;
    volatile ArrayList<Color[]> colors;
    volatile boolean Flash;
    volatile double FlashRateS;
    volatile double FlashTimer;
    volatile int colorSet;
    Thread thread;
    
    public static final int NumOfColors = 4;
    
    public LEDs()
    {
        spi = new SPI(Port.kOnboardCS0);
        spi.setClockRate(3000000);
        spi.setMSBFirst();
        latch = new DigitalOutput(Constants.LED_LATCH);

        colors = new ArrayList<Color[]>();
        colors.add(new Color[NumOfColors]);
        
        Flash = false;
        FlashTimer = 0;
        
        thread = new Thread(this);
    }
    
    public void start()
    {
        thread.start();
    }
    

    public synchronized void setFrame(LEDEffect.EFFECT effect, Color[] start, Color[] end)
    {
        colors.clear();
        colors.add(newColors);
        Flash = false;
    }

    public synchronized void setAll(Color color)
    {
        Color[] newColor = new Color[NumOfColors];
        colors.clear();
        
        for (int i = 0; i < NumOfColors; i++)
        {
            newColor[i] = color;
        }
        colors.add(newColor);
        Flash = false;
    }

    public void setAll(byte r, byte g, byte b)
    {
        this.setAll(new Color(r, g, b));
    }

    public synchronized void set(int i, Color color)
    {
        colors.get(0)[i] = color;
        Flash = false;
    }

    public void set(int i, byte r, byte g, byte b)
    {
        this.set(i, new Color(r, g, b));
    }

    public static String byteArrayToHex(byte[] a)
    {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
    
    public synchronized void FlashAll(ArrayList<Color[]> NewColors, double FlashRateS)
    {
        colors.clear();
        colors = NewColors;
        
        Flash = true;
        this.FlashRateS = FlashRateS;
    }
    
    public void run()
    {
        while (true)
        {
            updateColors();
        }
    }

    public synchronized void updateColors()
    {
        byte[] out = new byte[(int) (NumOfColors * 4.5)];

        if(!Flash)
        {
            for (int i = 0; i < NumOfColors; i++)
            {
                colors.get(0)[i].Get(i % 2 == 0 ? false : true, out, (int) (i * 4.5));
            }
        }
        else
        {
            
            if(Timer.getFPGATimestamp() - FlashTimer < FlashRateS)
            {
                for (int i = 0; i < NumOfColors; i++)
                {
                    colors.get(colorSet)[i].Get(i % 2 == 0 ? false : true, out, (int) (i * 4.5));
                }
            }
            else
            {
                FlashTimer = Timer.getFPGATimestamp();
                colorSet = (colorSet + 1) % colors.size();
            }
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

}
