package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.drive.HolonomicService;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LED;

public class StrafeToTarget extends Strafe
{
    RobotState state = RobotState.getInstance();
    final double tol;

    public StrafeToTarget(HolonomicService<?> helper, double xSpeed, double timeout, double tolerance)
    {
        super(helper, xSpeed, timeout);
        tol = tolerance;
    }

    protected void initialize()
    {
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected boolean isFinished()
    {
        if(state.visionInView && Math.abs(state.visionError) < tol)
        {
            return true;
        }
        return super.isFinished();
    }
}
