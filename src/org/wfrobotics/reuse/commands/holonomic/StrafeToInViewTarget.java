package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.LED;

public class StrafeToInViewTarget extends DriveCommand
{
    final SwerveSignal neutral = new SwerveSignal(new HerdVector(0, 0));
    final SwerveSignal s;
    final double tol;

    public StrafeToInViewTarget(double xSpeed, double tolerance)
    {
        requires(Robot.driveService.getSubsystem());
        s = new SwerveSignal(new HerdVector(xSpeed, 0));
        tol = tolerance;
    }

    protected void initialize()
    {
        super.initialize();
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected void execute()
    {
        Robot.driveService.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return !state.visionInView || Math.abs(state.visionError) < tol;
    }

    protected void end()
    {
        Robot.driveService.driveWithHeading(neutral);
    }
}
