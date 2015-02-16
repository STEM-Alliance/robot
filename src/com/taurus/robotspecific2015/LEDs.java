package com.taurus.robotspecific2015;

import com.sun.javafx.binding.StringFormatter;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

public class LEDs {// implements Runnable{

    SPI spi;
    DigitalOutput latch;
    Color colors[][];
    boolean Flash;
    double FlashRateS;
    double FlashTimer;
    int colorSet;
    
    
    public LEDs()
    {
        spi = new SPI(Port.kOnboardCS0);
        spi.setClockRate(3000000);
        spi.setMSBFirst();
        latch = new DigitalOutput(Constants.LED_LATCH);

        colors = new Color[2][4];
        Flash = false;
        FlashTimer = 0;
    }

    public void setAll(Color color)
    {
        for (int i = 0; i < colors[0].length; i++)
        {
            this.colors[0][i] = color;
        }
        Flash = false;
    }

    public void setAll(byte r, byte g, byte b)
    {
        this.setAll(new Color(r, g, b));
    }

    public void set(int i, Color color)
    {
        this.colors[0][i] = color;
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
    
    public void FlashAll(Color color1, Color color2, double FlashRateS)
    {
        for (int i = 0; i < colors[0].length; i++)
        {
            this.colors[0][i] = color1;
        }
        for (int i = 0; i < colors[1].length; i++)
        {
            this.colors[1][i] = color2;
        }
        
        Flash = true;
        this.FlashRateS = FlashRateS;
    }

    public void run()
    {
        byte[] out = new byte[(int) (colors[0].length * 4.5)];

        if(!Flash)
        {
            for (int i = 0; i < colors[0].length; i++)
            {
                colors[0][i].Get(i % 2 == 0 ? false : true, out, (int) (i * 4.5));
            }
        }
        else
        {
            
            if(Timer.getFPGATimestamp() - FlashTimer < FlashRateS)
            {
                for (int i = 0; i < colors[colorSet].length; i++)
                {
                    colors[colorSet][i].Get(i % 2 == 0 ? false : true, out, (int) (i * 4.5));
                }
            }
            else
            {
                FlashTimer = Timer.getFPGATimestamp();
                colorSet = (colorSet + 1) % colors.length;
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
