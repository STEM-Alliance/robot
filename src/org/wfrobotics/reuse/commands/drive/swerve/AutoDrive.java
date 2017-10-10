
package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AutoDrive extends Command
{
    private final SwerveSignal s;

    /**
     * Drive Normally
     * Details: Drive any magnitude and angle. Forward, backwards, sideways, at an angle.
     * Note: Use this constructor or the turn to angle one for the majority of autonomous driving
     * @param speedX Magnitude to move right (positive) and left (negative). (Range: -1 to 1)
     * @param speedY Magnitude to move forward (positive) and backward (negative). (Range: -1 to 1)
     * @param speedR Magnitude to turn clockwise (positive) or counterclockwise (negative) while driving. (Range: -1 to 1, Zero: Means don't spin)
     * @param timeout How long before this command finishes (Seconds)
     */
    public AutoDrive(double speedX, double speedY, double speedR, double timeout)
    {
        this(speedX, speedY, speedR, SwerveSignal.HEADING_IGNORE, timeout);
    }

    /**
     * Drive Halo
     * Details: Drive while simultaneously turning to (and thereafter maintaining) a specific field relative angle.
     * Note: A.K.A drive like your drive system is legit
     * @param speedX Magnitude to move right (positive) and left (negative). (Range: -1 to 1)
     * @param speedY Magnitude to move forward (positive) and backward (negative). (Range: -1 to 1)
     * @param speedR Magnitude to turn clockwise (positive) or counterclockwise (negative) while driving. (Range: -1 to 1, Zero: Means don't spin)
     * @param angle Angle to turn the robot to a field relative angle while driving (Range: -180 to 180, Units: Degrees)
     * @param timeout How long before this command finishes (Seconds)
     */
    public AutoDrive(double speedX, double speedY, double speedR, double angle, double timeout)
    {
        requires(Robot.driveSubsystem);
        s = new SwerveSignal(new SwerveSignal(new HerdVector(speedX, speedY), speedR, angle));
        setTimeout(timeout);
    }

    protected void execute()
    {
        Robot.driveSubsystem.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
