package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper.DriveSignal;
import org.wfrobotics.reuse.math.control.PID;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public class VisionDeploy extends Command
{
    private static final double kDistanceFromWallSlowDownInches = 10.0;
    private static final double kDistanceFromWallStopInches = 6.0;

    protected final RobotStateBase state = EnhancedRobot.getState();
    protected final TankSubsystem drive = TankSubsystem.getInstance();
    protected final EnhancedIO io = EnhancedRobot.getIO();
    protected final SuperStructure ss = SuperStructure.getInstance();
    protected static final CheesyDriveHelper helper = new CheesyDriveHelper();
    protected final PID pid = new PID(0.15, 0.00125);
    protected final double maxThrottle;


    public VisionDeploy(double maxSpeed)
    {
        requires(drive);
        this.maxThrottle = maxSpeed;
    }

    protected void initialize()
    {
        drive.setBrake(true);
    }

    protected void execute()
    {
        final double turnCorrection = getVisionCorrection();
        final double turn = io.getTurn() + turnCorrection;
        final boolean slowDown = ss.getDistanceFromWall() < kDistanceFromWallSlowDownInches;
        final double throttle = (slowDown) ? maxThrottle / 2.0 : maxThrottle;  // TODO util.scaleToRange() instead

        final DriveSignal s = helper.cheesyDrive(throttle, turn, true, false);
        drive.driveOpenLoop(s.getLeft(), s.getRight());
    }

    protected boolean isFinished()
    {
        return !state.visionInView || ss.getDistanceFromWall() < kDistanceFromWallStopInches;
    }

    private double getVisionCorrection()
    {
        double visionAngle = 0.0;

        if (state.visionInView)
        {
            final double error = state.getVisionError();
            final double sign = Math.signum(error);

            if (Math.abs(error) < .02)  // Don't wind up if really close
            {
                pid.reset();
            }

            visionAngle = pid.update(timeSinceInitialized(), sign);
        }
        else
        {
            pid.reset();
        }
        return visionAngle;
    }
}
