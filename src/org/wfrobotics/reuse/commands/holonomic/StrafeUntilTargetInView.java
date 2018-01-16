package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.drive.HolonomicService;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LED;

public class StrafeUntilTargetInView extends Strafe
{
    RobotState state = RobotState.getInstance();

    public StrafeUntilTargetInView(HolonomicService<?> helper, double xSpeed, double timeout)
    {
        super(helper, xSpeed, timeout);
    }

    protected void initialize()
    {
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
