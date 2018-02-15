package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.commands.LEDSet;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;

public class LEDsubsystem extends Subsystem
{

    public static Spark LedController;


    public static Effect defaultLEDEffect = new Effect(EFFECT_TYPE.FADE, LEDs.COLORS_THE_HERD, 1);

    public LEDsubsystem ()
    {
        LedController = new Spark(5);
    }
    public void LEDSet (double value)
    {
        LedController.set(value);

    }


    //    public Effect getAllianceEffect()
    //    {
    //        DriverStation.Alliance team = DriverStation.getInstance().getAlliance();
    //        Color[] teamDefaultColors = (team == DriverStation.Alliance.Red) ? LEDs.COLORS_RED_ALLIANCE : LEDs.COLORS_BLUE_ALLIANCE;
    //
    //        return new Effect(EFFECT_TYPE.CYCLE, teamDefaultColors, 1);
    //    }
    protected void initDefaultCommand()
    {
        setDefaultCommand(new LEDSet(-0.37));
    }
}
