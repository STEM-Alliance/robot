package org.wfrobotics.robot.commands.drive;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper.DriveSignal;
import org.wfrobotics.reuse.math.control.PID;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public class DriveToTarget extends Command
{
    private final double kVisionDeadband = 0.02;
    private final double kLinkToShinyDegrees;

    private final boolean kBrakeMode;
    private final Elevator elevator = Elevator.getInstance();
    private final Link link = Link.getInstance();
    private final SuperStructure ss = SuperStructure.getInstance();
    private final TankSubsystem drive = TankSubsystem.getInstance();
    private final EnhancedIO io = EnhancedRobot.getIO();
    private static final CheesyDriveHelper helper = new CheesyDriveHelper();
    private final PID pid;
    // protected final PID pid = new PID(0.025, 0.004, 0.0);  // Tuned pretty good for coast mode
    // protected final PID pid = new PID(0.16, 0.00001, 0.0);  // Tuned well for brake mode
    // protected final PID pid = new PID(0.008, 0.0008, 0.0);  // Tuned well for actual PID implementation

    public DriveToTarget()
    {
        requires(drive);

        final RobotConfig config = RobotConfig.getInstance();
        
        kLinkToShinyDegrees = config.kVisionLinkAngleToShiny;
        kBrakeMode = config.kVisionBrakeMode;

        Preferences prefs = Preferences.getInstance();
        double p = prefs.getDouble("p", 0.0);
        double i = prefs.getDouble("i", 0.0);
        double d = prefs.getDouble("d", 0.0);
        double izone = prefs.getDouble("izone", 0.0);
        pid = new PID(p, i, d, izone);
        //pid = new PID(config.kVisionP, config.kVisionI, config.kVisionD, config.kVisionIZone);
    }

    protected void initialize()
    {
        drive.setBrake(kBrakeMode);
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
        final boolean elevatorObstructing = elevator.getPosition() > 25.0;
        final boolean linkToShiny = link.getPosition() < kLinkToShinyDegrees;
        double visionAngle = 0.0;

        if (elevatorObstructing || linkToShiny)  // TODO - Switch between cameras instead
        {
            pid.reset();
        }
        else if (ss.getTapeInView())
        {
            // final double error = ss.getTapeYaw();
            // visionAngle = pid.update(timeSinceInitialized(), error);

            final double error = ss.getTapeYaw();
            final double sign = Math.signum(error);

            if (Math.abs(error) < kVisionDeadband)  // Don't wind up if really close
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
