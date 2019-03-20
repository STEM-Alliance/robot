package org.wfrobotics.robot.commands.drive;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper;
import org.wfrobotics.reuse.math.control.CheesyDriveHelper.DriveSignal;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public final class DriveToTarget extends Command
{
    private final boolean kBrakeMode;
    private final double kLinkToShinyDegrees;
    private final double kVisionDeadband = 0.02;

    private final Elevator elevator = Elevator.getInstance();
    private final Link link = Link.getInstance();
    private final SuperStructure ss = SuperStructure.getInstance();
    private final TankSubsystem drive = TankSubsystem.getInstance();
    private final EnhancedIO io = EnhancedRobot.getIO();
    private static final CheesyDriveHelper helper = new CheesyDriveHelper();
    private final SimplePID pid;
    // protected final SimplePID pid = new SimplePID(0.025, 0.004, 0.0);  // Tuned pretty good for coast mode
    // protected final SimplePID pid = new SimplePID(0.16, 0.00001, 0.0);  // Tuned well for brake mode
    // protected final PID pid = new PID(0.008, 0.0008, 0.0);  // Tuned well for actual PID implementation

    public DriveToTarget()
    {
        requires(drive);

        final RobotConfig config = RobotConfig.getInstance();
        final Preferences prefs = Preferences.getInstance();
        final double p = prefs.getDouble("p", config.kVisionP);
        final double i = prefs.getDouble("i", config.kVisionI);
        // final double d = prefs.getDouble("d", config.kVisionD);
        // final double izone = prefs.getDouble("izone", config.kVisionIZone);

        kLinkToShinyDegrees = config.kVisionLinkAngleToShiny;
        kBrakeMode = config.kVisionBrakeMode;

        pid = new SimplePID(p, i);
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

            visionAngle = pid.update(sign);
        }
        else
        {
            pid.reset();
        }

        return visionAngle;
    }

    private final class SimplePID
    {
        private final double kP;
        private final double kI;
        private double accum;

        /** Proportional, Integral, Derivative */
        public SimplePID(double p, double i)
        {
            kP = p;
            kI = i;
            reset();
        }

        /** Input new error, returns new motor output to reduce the error based on PID constants */
        public double update(double error)
        {
            accum += error;
            return kP * error + kI * accum;
        }

        /** Call when your setpoint (desired value) changes */
        public void reset()
        {
            accum = 0.0;
        }
    }
}
