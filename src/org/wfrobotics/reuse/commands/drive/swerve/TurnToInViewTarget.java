package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LED;

import edu.wpi.first.wpilibj.command.Command;

/** Turn until reaching the target, assumes target in view at start **/
public class TurnToInViewTarget extends Command
{
    RobotState state = RobotState.getInstance();
    HerdVector v = new HerdVector(0, 0);
    final double tol;

    public TurnToInViewTarget(double tolerance)
    {
        requires(Robot.driveSubsystem);
        tol = tolerance;
    }

    protected void initialize()
    {
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected void execute()
    {
        double targetHeading = state.robotHeading + state.getVisionError();
        SwerveSignal s = new SwerveSignal(v, 0, targetHeading);
        Robot.driveSubsystem.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return !state.visionInView || Math.abs(state.robotHeading + state.getVisionError()) < tol;
    }

    protected void end()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0));
    }
}