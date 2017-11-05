package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

// TODO Should autodrive switch or optionally have polar?
// TODO Put field relative as constructor boolean to rotate drive signal here, not as drivetrain mode

public class AutoDrive extends Command
{
    private final SwerveSignal s;

    /** Drive, optionally while simultaneously turning at a fixed rate */
    public AutoDrive(double speedX, double speedY, double speedR, double timeout)
    {
        this(speedX, speedY, speedR, SwerveSignal.HEADING_IGNORE, timeout);
    }

    /** Drive, optionally while simultaneously turning to a specific angle */
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

    protected void end()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0));
    }
}