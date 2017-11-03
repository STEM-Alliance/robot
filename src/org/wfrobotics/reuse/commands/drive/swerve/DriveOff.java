package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveOff extends Command
{
    HerdLogger log = new HerdLogger(DriveOff.class);
    SwerveSignal s = new SwerveSignal(new HerdVector(0, 0), 0);

    public DriveOff()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        log.info("Drive Mode", "Off");
    }

    protected void execute()
    {
        log.info("Drive Cmd", s.velocity);
        Robot.driveSubsystem.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
