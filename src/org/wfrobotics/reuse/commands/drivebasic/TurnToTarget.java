package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.subsystems.LED;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public class TurnToTarget extends TurnToHeading
{
    public TurnToTarget(DriveService<?> helper, double headingFieldRelative, double tolerance)
    {
        super(helper, headingFieldRelative, tolerance);
    }

    protected void initialize()
    {
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected void execute()
    {
        if (state.visionInView)
        {
            double targetHeading = state.robotHeading + state.visionError;
            vector = new HerdVector(1, targetHeading);  // TODO magnitude should be configurable, add to constructor?
        }
        super.execute();
    }

    protected boolean isFinished()
    {
        if (state.visionInView)
        {
            return Math.abs(state.visionError) < tol;
        }
        return super.isFinished();
    }
}
