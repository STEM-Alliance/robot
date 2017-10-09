package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.subsystems.LED;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public class TurnToTarget extends TurnToHeading
{
    public TurnToTarget(double headingFieldRelative, double tolerance)
    {
        super(headingFieldRelative, tolerance);
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
            s = new SwerveSignal(new HerdVector(0, 0), 0, targetHeading);
        }
        super.execute();
    }

    protected boolean isFinished()
    {
        if (state.visionInView)
        {
            return state.getVisionError() < tol;
        }
        return super.isFinished();
    }
}
