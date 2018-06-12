package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Color;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.hardware.led.MindsensorCANLight;

import edu.wpi.first.wpilibj.DriverStation;

public class LED extends MindsensorCANLight
{
    private static LED instance = null;

    public static Effect defaultLEDEffect = new Effect(EFFECT_TYPE.FADE, LEDs.COLORS_THE_HERD, 1);

    public LED(int address)
    {
        super(address);
    }

    public static LED getInstance()
    {
        if (instance == null) { instance = new LED(0); }
        return instance;
    }

    public Effect getAllianceEffect()
    {
        DriverStation.Alliance team = DriverStation.getInstance().getAlliance();
        Color[] teamDefaultColors = (team == DriverStation.Alliance.Red) ? LEDs.COLORS_RED_ALLIANCE : LEDs.COLORS_BLUE_ALLIANCE;

        return new Effect(EFFECT_TYPE.CYCLE, teamDefaultColors, 1);
    }
}
