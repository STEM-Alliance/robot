package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.LED;

/** Turn until reaching the target, assumes target in view at start **/
public class TurnToInViewTarget extends DriveCommand
{
    final HerdVector neutral = new HerdVector(0, 0);
    final double tol;

    public TurnToInViewTarget(double tolerance)
    {
        requires(Robot.driveService.getSubsystem());
        tol = tolerance;
    }

    protected void initialize()
    {
        super.initialize();
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected void execute()
    {
        double targetHeading = state.robotHeading + state.visionError;
        HerdVector v = new HerdVector(1, targetHeading);  // TODO magnitude should be configurable, add to constructor?
        Robot.driveService.turnBasic(v);
    }

    protected boolean isFinished()
    {
        return !state.visionInView || Math.abs(state.visionError) < tol;
    }

    protected void end()
    {
        Robot.driveService.turnBasic(neutral);
    }
}