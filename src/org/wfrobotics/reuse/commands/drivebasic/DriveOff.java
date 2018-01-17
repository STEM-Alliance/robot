package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

/** Safety command for drivetrain. Toggle or cancel to quit **/
public class DriveOff extends DriveCommand
{
    SwerveSignal s = new SwerveSignal(new HerdVector(0, 0));

    public DriveOff()
    {
        requires(Robot.driveService.getSubsystem());
    }

    protected void execute()
    {
        log.info("Drive Cmd", s.velocity);
        Robot.driveService.driveBasic(new HerdVector(0, 0));
    }

    protected boolean isFinished()
    {
        return false;
    }
}
