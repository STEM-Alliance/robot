package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper.DriveSignal;
import org.wfrobotics.reuse.math.control.PID;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public class DriveToTarget extends Command
{
    // protected final RobotStateBase state = EnhancedRobot.getState();
    protected final SuperStructure ss = SuperStructure.getInstance();
    protected final TankSubsystem drive = TankSubsystem.getInstance();
    protected final EnhancedIO io = EnhancedRobot.getIO();
    protected static final CheesyDriveHelper helper = new CheesyDriveHelper();
    protected final PID pid = new PID(0.025, 0.004);  // Tuned pretty good for coast mode
    // protected final PID pid = new PID(0.16, 0.00001);  // Tuned well for brake mode

    public DriveToTarget()
    {
        requires(drive);
    }

    protected void initialize()
    {
        drive.setBrake(false);  // Coast
        // drive.setBrake(true);  // Brake
    }

    protected void execute()
    {
        final double turnCorrection = getVisionCorrection();
        final double turn = io.getTurn() + turnCorrection;

        final DriveSignal s = helper.cheesyDrive(io.getThrottle(), turn, io.getDriveQuickTurn(), false);
        drive.driveOpenLoop(s.getLeft(), s.getRight());
    }

    protected boolean isFinished()
    {
        return false;
    }

    private double getVisionCorrection()
    {
        double visionAngle = 0.0;

        if (ss.getTapeInView())
        {
            final double error = ss.getTapeYaw();
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
