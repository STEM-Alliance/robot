package org.wfrobotics.commands;

import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class LEDSignal extends Command 
{
    public LEDSignal(double timeout)
    {
        this.setTimeout(timeout);
    }
    
    protected void initialize()
    {
        Robot.leds.set(new Effect(EFFECT_TYPE.BLINK, LEDs.RED, .15));
    }
    
    protected boolean isFinished()
    {
        return isTimedOut();
    }
    
    protected void end()
    {
        Robot.leds.set(Robot.defaultLEDEffect);
    }
}