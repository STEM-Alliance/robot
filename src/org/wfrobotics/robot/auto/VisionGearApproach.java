package org.wfrobotics.robot.auto;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LED;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearApproach extends Command
{
    private RobotState state = RobotState.getInstance();
    private PIDController pidX;
    private boolean fieldRelative = true;

    private SwerveSignal s;

    private boolean done;

    public VisionGearApproach()
    {
        requires(Robot.driveSubsystem);
        pidX = new PIDController(2.5, 0.125, 0, .35);
        s = new SwerveSignal(new HerdVector(0, 0), 0, SwerveSignal.HEADING_IGNORE);
    }

    protected void initialize()
    {
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
        fieldRelative = Robot.driveSubsystem.getFieldRelative();
        Robot.driveSubsystem.setFieldRelative(false);
        done = false;
    }

    protected void execute()
    {
        double distanceFromCenter = state.visionError;
        double visionWidth = state.visionWidth;
        double valueY = -.315;
        double valueX = 0;
        boolean found = state.visionInView;

        SmartDashboard.putBoolean("GearFound", found);

        if(!found)
        {
            done = true;
            return;
        }

        // we think we've found at least one target, so get an estimate speed to line us up
        valueX = pidX.update(distanceFromCenter);

        if(Math.abs(distanceFromCenter) < .05)
        {
            // if we started really far away from center, this will then reduce that overshoot maybe
            pidX.resetError();
        }

        // we can still see a target
        if(visionWidth > 15 && visionWidth < 335)
        {
            s = new SwerveSignal(new HerdVector(valueY, valueX), 0, SwerveSignal.HEADING_IGNORE);
        }
        else
        {
            // if the detected target width is less than 15, we're either at the target or something went wrong
            done = true;
        }

        Utilities.PrintCommand("VisionGearDetect", this, state.visionInView + "");
        SmartDashboard.putNumber("GearDistanceX", distanceFromCenter);
        SmartDashboard.putNumber("VisionWidth", visionWidth);
        SmartDashboard.putNumber("VisionGearY", valueY);
        SmartDashboard.putNumber("VisionGearX", valueX);

        Robot.driveSubsystem.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return done;
    }

    protected void end()
    {
        Robot.driveSubsystem.setFieldRelative(fieldRelative);
        LED.getInstance().set(LED.defaultLEDEffect);
    }

    protected void interrupted()
    {
        end();
    }
}
