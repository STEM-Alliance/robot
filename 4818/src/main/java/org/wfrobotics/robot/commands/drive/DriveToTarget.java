package org.wfrobotics.robot.commands.drive;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper.DriveSignal;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Vision;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public final class DriveToTarget extends Command
{
    private static final double kVisionIZone = 25.0;  // Degrees Error

    private final Vision vision = Vision.getInstance();
    private final TankSubsystem drive = TankSubsystem.getInstance();
    private final EnhancedIO io = EnhancedRobot.getIO();
    private static final CheesyDriveHelper helper = new CheesyDriveHelper();
    private final PID pid;
    // private final SimplePID pid = new SimplePID(0.025, 0.004);  // Tuned pretty good for coast mode
    // private final SimplePID pid = new SimplePID(0.16, 0.00001);  // Tuned well for brake mode

    public DriveToTarget()
    {
        requires(drive);

        final RobotConfig config = RobotConfig.getInstance();
        final Preferences prefs = Preferences.getInstance();
        final double p = prefs.getDouble("p", config.kVisionP);
        final double i = prefs.getDouble("i", config.kVisionI);
        final double d = prefs.getDouble("d", config.kVisionD);

        pid = new PID(p, i, d, kVisionIZone, 0.02);
    }

    protected void initialize()
    {
        drive.setBrake(false);
    }

    protected void execute()
    {
        final boolean quickTurn = io.getDriveQuickTurn() || (driverPassive() && visionAvailable());  // Can turn in place
        final double quickTurnCompensation = (quickTurn) ? 1.0 : 2.0;  // Normal turn needs more "I" than quick turn

        final double turnCorrection = getVisionCorrection() * quickTurnCompensation;
        final double turn = io.getTurn() + turnCorrection;
        
        final DriveSignal s = helper.cheesyDrive(io.getThrottle(), turn, quickTurn, false);
        drive.driveOpenLoop(s.getLeft(), s.getRight());
    }

    protected boolean isFinished()
    {
        return false;
    }

    private double getVisionCorrection()
    {
        double percentOutput = 0.0;

        if (!visionAvailable())
        {
            pid.reset();
            return percentOutput;
        }

        final double error = vision.getError();
        final double sign = Math.signum(error);
        final double correction = pid.update(sign);

        if (Math.abs(correction) > 0.01)
        {
            percentOutput = correction;
        }
        else
        {
            pid.reset();
        }

        return percentOutput;
    }

    private boolean driverPassive()
    {
        return Math.abs(io.getThrottle()) < 0.02 && Math.abs(io.getTurn()) < 0.02;
    }

    private boolean visionAvailable()
    {
        return vision.getModeTape() && vision.getInView();
    }
}
