package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.hardware.led.MindsensorCANLight;
import org.wfrobotics.robot.config.RobotMap;

public class LED extends MindsensorCANLight
{
    private static LED instance = null;

    public static Effect defaultLEDEffect = new Effect(EFFECT_TYPE.FADE, LEDs.COLORS_THE_HERD, 1);

    public LED(int address)
    {
        super(address);
        enable(true);
    }

    public static LED getInstance()
    {
        if (instance == null) { instance = new LED(RobotMap.CAN_LIGHT); }
        return instance;
    }
}
