package com.taurus.robotspecific2015;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

public class LEDs implements Runnable {

    private SPI spi;
    private DigitalOutput latch;
    private volatile  ConcurrentLinkedQueue<LEDEffect> effectQueue;
    private Thread thread;
    private boolean StopRequested = false;
    
    public static final int NumOfColors = 4;
    public static final long TimeService = 100;  // milliseconds
    
    public LEDs()
    {
        Color[] DefaultBlack = new Color[NumOfColors];
        ArrayList<Color[]> DefaultColors = new ArrayList<Color[]>();        
        LEDEffect DefaultEffect;
        
        // Hardware
        spi = new SPI(Port.kOnboardCS0);
        spi.setClockRate(3000000);
        spi.setMSBFirst();
        latch = new DigitalOutput(Constants.LED_LATCH);

        // Effects/Colors
        for (int index = 0; index < DefaultBlack.length; index++)
        {
            DefaultBlack[index] = Color.Black;
        }
        DefaultColors.add(DefaultBlack);
        DefaultEffect = new LEDEffect(DefaultColors, LEDEffect.EFFECT.SOLID, Double.MAX_VALUE, Double.MAX_VALUE);
        effectQueue = new ConcurrentLinkedQueue<LEDEffect>();        
        effectQueue.add(DefaultEffect);
        
        thread = new Thread(this);
    }
    
    // Start service thread for LEDs
    public void start()
    {
        thread.start();
    }

    public void AddEffect(LEDEffect effect, boolean clearEffects)
    {
        if (clearEffects)
        {
            effectQueue.clear();
        }
        effectQueue.add(effect);
        
        this.notify();  // Wake the LED thread with an interrupt
    }
    
    @SuppressWarnings("null")
    public void run()
    {
        LEDEffect currentEffect = null;
        
        while (!StopRequested)
        {
            long waitTime = Long.MAX_VALUE;

            // Is there an effect to run?                
            if (currentEffect == null)
            {
                if (effectQueue.peek() != null)
                {
                    currentEffect = effectQueue.poll();
                }
            }
            else if (!currentEffect.isDone())
            {
                updateColors(currentEffect.getColors());
                waitTime = TimeService;
            }
            else
            {
                currentEffect = null;  // Current effect is done
            }
            
            // Wait for timeout or interrupt from another thread adding an effect
            try
            {
                this.wait(waitTime);  // Wait (until interrupt) for another thread to add an effect
            }
            catch (InterruptedException e)
            {
                // We timed out or were interrupted while waiting
            }
        }
    }
    
    public void stop()
    {
        StopRequested = true;
    }
    
    private static String byteArrayToHex(byte[] a)
    {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
    
    private void updateColors(Color[] colors)
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
}