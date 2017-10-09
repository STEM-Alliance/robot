package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LED;

public class StrafeUntilTargetInView extends Strafe
{
    RobotState state = RobotState.getInstance();

    public StrafeUntilTargetInView(double xSpeed, double timeout)
    {
        super(xSpeed, timeout);
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
