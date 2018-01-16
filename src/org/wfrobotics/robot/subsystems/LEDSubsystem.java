package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Color;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.hardware.led.MindsensorCANLight;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.DriverStation;

public class LEDSubsystem extends MindsensorCANLight
{
    private static LEDSubsystem instance = null;

    public static Effect defaultLEDEffect = new Effect(EFFECT_TYPE.FADE, LEDs.COLORS_THE_HERD, 1);

    public LEDSubsystem(int address)
    {
        super(address);
    }

    public static LEDSubsystem getInstance()
    {
        if (instance == null) { instance = new LEDSubsystem(RobotMap.CAN_LIGHT); }
        return instance;
    }

    public Effect getAllianceEffect()
    {
        DriverStation.Alliance team = DriverStation.getInstance().getAlliance();
        Color[] teamDefaultColors = (team == DriverStation.Alliance.Red) ? LEDs.COLORS_RED_ALLIANCE : LEDs.COLORS_BLUE_ALLIANCE;

        return new Effect(EFFECT_TYPE.CYCLE, teamDefaultColors, 1);
    }
}
