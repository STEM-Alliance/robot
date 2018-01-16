package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.drive.HolonomicService;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LED;

import edu.wpi.first.wpilibj.command.Command;

public class StrafeToInViewTarget extends Command
{
    RobotState state = RobotState.getInstance();
    protected HolonomicService<?> driveHelper;
    final SwerveSignal s;
    final double tol;

    public StrafeToInViewTarget(HolonomicService<?> helper, double xSpeed, double tolerance)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
        s = new SwerveSignal(new HerdVector(xSpeed, 0));
        tol = tolerance;
    }

    protected void initialize()
    {
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected void execute()
    {
        driveHelper.getDrive().driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return !state.visionInView || Math.abs(state.visionError) < tol;
    }

    protected void end()
    {
        driveHelper.getDrive().driveWithHeading(new SwerveSignal(new HerdVector(0, 0)));
    }
}
