package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LED;

import edu.wpi.first.wpilibj.command.Command;

/** Turn until reaching the target, assumes target in view at start **/
public class TurnToInViewTarget extends Command
{
    RobotState state = RobotState.getInstance();
    protected DriveService<?> driveHelper;
    final double tol;

    public TurnToInViewTarget(DriveService<?> helper, double tolerance)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
        tol = tolerance;
    }

    protected void initialize()
    {
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected void execute()
    {

        double targetHeading = state.robotHeading + state.visionError;
        HerdVector v = new HerdVector(1, targetHeading);  // TODO magnitude should be configurable, add to constructor?
        driveHelper.getDrive().turnBasic(v);
    }

    protected boolean isFinished()
    {
        return !state.visionInView || Math.abs(state.visionError) < tol;
    }

    protected void end()
    {
        driveHelper.getDrive().turnBasic(new HerdVector(0, 0));
    }
}