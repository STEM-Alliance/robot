package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LEDSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class StrafeToInViewTarget extends Command
{
    RobotState state = RobotState.getInstance();
    final SwerveSignal s;
    final double tol;

    public StrafeToInViewTarget(double xSpeed, double tolerance)
    {
        requires(Robot.driveSubsystem);
        s = new SwerveSignal(new HerdVector(xSpeed, 0));
        tol = tolerance;
    }

    protected void initialize()
    {
        LEDSubsystem.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected void execute()
    {
        Robot.driveSubsystem.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return !state.visionInView || Math.abs(state.visionError) < tol;
    }

    protected void end()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0)));
    }
}
