package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/** Stay put until another command uses the drive subsystem */
public class AutoDriveWait extends Command
{
    public AutoDriveWait()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0));
    }

    protected boolean isFinished()
    {
        return true;
    }
}