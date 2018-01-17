package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.subsystems.LED;

public class StrafeUntilTargetInView extends Strafe
{
    public StrafeUntilTargetInView(double xSpeed, double timeout)
    {
        super(xSpeed, timeout);
    }

    protected void initialize()
    {
        super.initialize();
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected boolean isFinished()
    {
        if(state.visionInView)
        {
            return true;
        }
        return super.isFinished();
    }
}
