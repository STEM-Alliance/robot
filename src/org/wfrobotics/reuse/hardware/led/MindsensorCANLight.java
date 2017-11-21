package org.wfrobotics.reuse.hardware.led;

import org.wfrobotics.reuse.hardware.led.LEDs.Color;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.LEDController;
import org.wfrobotics.reuse.utilities.HerdLogger;

import com.mindsensors.CANLight;

public class MindsensorCANLight implements LEDController
{
    private HerdLogger log = new HerdLogger(LEDController.class);
    private CANLight controller; //http://mindsensors.com/largefiles/CANLight/CANLight_Demo.java
    public boolean enabled;

    public MindsensorCANLight(int address)
    {
//        try
//        {
//            controller = new CANLight(address);
//            enabled = true;
//        }
//        catch(Exception e)
//        {
//            log.warning("LEDController", "Cannot create CANLight for address " + String.valueOf(address));
//            enabled = false;
//        }
    }

    public void set(Effect e)
    {
//        set(e.type, e.colors, e.time);
    }

    public void set(Effect.EFFECT_TYPE type, Color[] colors, double time)
    {
//        if (!enabled)
//        {
//            return;
//        }
//        if (colors.length > 8)
//        {
//            log.warning("LEDController", "Hardware cannot support > 8 colors");
//            return;
//        }
//
//        for (int index = 0; index < colors.length; index++)
//        {
//            controller.writeRegister(index, time, colors[index].r, colors[index].g, colors[index].b);
//        }
//
//        switch (type)
//        {
//            case CYCLE:
//                controller.cycle(0, colors.length - 1);
//                break;
//            case FADE:
//                controller.fade(0, colors.length - 1);
//                break;
//            case BLINK:
//                controller.flash(0);
//                break;
//            case OFF:
//                controller.showRGB(0, 0, 0);
//                break;
//            case SOLID:
//            default:
//                controller.showRegister(0);
//                break;
//        }
    }

    public void enable(boolean enable)
    {
//        enabled = enable;
//        if (enabled == false && controller != null) { controller.showRGB(0, 0, 0); }
    }
}
