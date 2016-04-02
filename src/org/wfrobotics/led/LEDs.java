package org.wfrobotics.led;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LEDs implements Runnable {

    private final Hardware controller;
    private final Thread thread;
    private final int NumColors;
    private volatile  ConcurrentLinkedQueue<Effect> effectQueue;
    private boolean StopRequested = false;
    
    public LEDs(Hardware ledcontroller, int numOfColors, int priority)
    {
        ArrayList<Color[]> DefaultColors = new ArrayList<Color[]>();        
        Effect DefaultEffect;
        Color[] DefaultBlack;
        
        NumColors = numOfColors;
        DefaultBlack = new Color[NumColors];
        
        // Hardware
        controller = ledcontroller;
        controller.init();

        // Effects/Colors
        for (int index = 0; index < DefaultBlack.length; index++)
        {
            DefaultBlack[index] = Color.Black;
        }
        DefaultColors.add(DefaultBlack);
        DefaultEffect = new Effect(DefaultColors, Effect.EFFECT.SOLID, Double.MAX_VALUE, Double.MAX_VALUE);
        effectQueue = new ConcurrentLinkedQueue<Effect>();        
        effectQueue.add(DefaultEffect);
        
        thread = new Thread(this);
        thread.setPriority(priority);
    }
    
    // Start service thread for LEDs
    public void start()
    {
        thread.start();
    }

    public void AddEffect(Effect effect, boolean clearEffects)
    {
        if (clearEffects)
        {
            effectQueue.clear();
        }
        effectQueue.add(effect);
        
        // Future: Wake the LED thread with an interrupt
    }

    public void run()
    {
        Effect currentEffect = null;
        
        while (!StopRequested)
        {
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
                controller.update(currentEffect.getColors());
            }
            else
            {
                currentEffect = null;  // Current effect is done
            }
            
            // Future: Wait for timeout or interrupt from another thread adding an effect
        }
    }
    
    public void stop()
    {
        StopRequested = true;
    }
}