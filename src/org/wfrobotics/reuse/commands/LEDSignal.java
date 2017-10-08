package org.wfrobotics.reuse.commands;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.subsystems.LED;

import edu.wpi.first.wpilibj.command.Command;

public class LEDSignal extends Command
{
    public LEDSignal(double timeout)
    {
        setTimeout(timeout);
    }

    protected void initialize()
    {
        LED.getInstance().set(new Effect(EFFECT_TYPE.BLINK, LEDs.RED, .15));
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end()
    {
        LED.getInstance().set(LED.defaultLEDEffect);
    }
}