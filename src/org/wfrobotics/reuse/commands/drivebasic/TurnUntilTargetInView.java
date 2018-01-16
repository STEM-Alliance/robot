package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.robot.subsystems.LED;

/** Turn until we see the target, or get to the expected heading it should be at **/
public class TurnUntilTargetInView extends TurnToHeading
{
    public TurnUntilTargetInView(DriveService<?> helper, double headingFieldRelative, double tolerance)
    {
        super(helper, headingFieldRelative, tolerance);
    }

    protected void initialize()
    {
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected boolean isFinished()
    {
        if (state.visionInView)
        {
            return true;
        }
        return super.isFinished();
    }
}
