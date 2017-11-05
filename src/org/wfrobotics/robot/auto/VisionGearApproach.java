package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LED;

import edu.wpi.first.wpilibj.command.Command;

public class VisionGearApproach extends Command
{
    private HerdLogger log = new HerdLogger(VisionGearApproach.class);
    private RobotState state = RobotState.getInstance();
    private PIDController pidX;
    private SwerveSignal s;

    public VisionGearApproach()
    {
        requires(Robot.driveSubsystem);
        pidX = new PIDController(2.5, 0.125, 0, .35);
        s = new SwerveSignal(new HerdVector(0, 0), 0, SwerveSignal.HEADING_IGNORE);
    }

    protected void initialize()
    {
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }

    protected void execute()
    {
        double distanceFromCenter = state.visionError;
        double visionWidth = state.visionWidth;
        double valueY = -.315;
        double valueX = 0;

        // We think we've found at least one target, so get an estimate speed to line us up
        valueX = pidX.update(distanceFromCenter);

        if(Math.abs(distanceFromCenter) < .05)
        {
            // If we started really far away from center, this will then reduce that overshoot maybe
            pidX.resetError();
        }

        // We can still see a target
        if(visionWidth > 15 && visionWidth < 335)
        {
            HerdVector v = new HerdVector(valueY, valueX);
            v = v.rotate(-state.robotHeading);  // Field relative
            s = new SwerveSignal(v, 0, SwerveSignal.HEADING_IGNORE);
        }

        log.info("GearFound", state.visionInView);
        log.debug("GearDistanceX", distanceFromCenter);
        log.debug("VisionWidth", visionWidth);
        log.debug("VisionGearY", valueY);
        log.debug("VisionGearX", valueX);

        Robot.driveSubsystem.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        boolean found = state.visionInView;
        double visionWidth = state.visionWidth;

        log.info("GearFound", found);

        return !found || visionWidth < 15 || visionWidth > 335;
    }

    protected void end()
    {
        LED.getInstance().set(LED.defaultLEDEffect);
    }

    protected void interrupted()
    {
        end();
    }
}
