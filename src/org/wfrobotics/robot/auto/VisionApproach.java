package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

public class VisionApproach extends Command
{
    private HerdLogger log = new HerdLogger(VisionApproach.class);
    private RobotState state = RobotState.getInstance();
    private PIDController pidX;

    public VisionApproach()
    {
        requires(Robot.driveService.getSubsystem());
        pidX = new PIDController(2.5, 0.125, 0, .35);
    }

    protected void initialize()
    {

    }

    protected void execute()
    {
        double distanceFromCenter = state.visionError;
        double valueY = -.315;
        double valueX = 0;
        SwerveSignal s;

        // We think we've found at least one target, so get an estimate speed to line us up
        valueX = pidX.update(distanceFromCenter);

        if(Math.abs(distanceFromCenter) < .05)
        {
            // If we started really far away from center, this will then reduce that overshoot maybe
            pidX.resetError();
        }

        HerdVector v = new HerdVector(valueY, valueX);
        v = v.rotate(-state.robotHeading);  // Field relative
        s = new SwerveSignal(v, 0, SwerveSignal.HEADING_IGNORE);

        log.debug("VisionDistanceX", distanceFromCenter);
        log.debug("VisionY", valueY);
        log.debug("VisionX", valueX);

        Robot.driveService.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        boolean found = state.visionInView;
        double visionWidth = state.visionWidth;

        log.info("TargetsFound", found);
        log.debug("VisionWidth", visionWidth);

        return !found || visionWidth < 15 || visionWidth > 335;
    }

    protected void end()
    {

    }

    protected void interrupted()
    {
        end();
    }
}
