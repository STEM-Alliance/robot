package org.wfrobotics.hardware.led;

import org.wfrobotics.hardware.led.LEDs.*;

import com.mindsensors.CANLight;

import edu.wpi.first.wpilibj.DriverStation;

public class MindsensorCANLight implements LEDController
{
    private final CANLight controller; //http://mindsensors.com/largefiles/CANLight/CANLight_Demo.java
    public boolean enabled; 

    public MindsensorCANLight(int address)
    {            
        controller = new CANLight(address);
        enabled = true;
    }

    public void set(Effect e)
    {
        set(e.type, e.colors, e.time);
    }

    public void set(Effect.EFFECT_TYPE type, Color[] colors, double time)
    {
        if (!enabled)
        {
            return;
        }        
        if (colors.length > 8)
        {
            DriverStation.reportWarning("CANLight hardware cannot support > 8 colors", true);
            return;
        }        
        
        for (int index = 0; index < colors.length; index++)
        {
            controller.writeRegister(index, time, colors[index].r, colors[index].g, colors[index].b);
        }

        switch (type)
        {
            case CYCLE:
                controller.cycle(0, colors.length - 1);
                break;
            case FADE:
                controller.fade(0, colors.length - 1);
                break;
            case BLINK:
                controller.flash(0);
                break;
            case OFF:
                controller.showRGB(0, 0, 0);
                break;
            case SOLID:
            default:
                controller.showRegister(0);
                break;
        }
    }
    
    public void enable(boolean enable)
    {
        enabled = enable;
    }
    
    public void off()
    {
        controller.showRGB(0, 0, 0);
    }
}
