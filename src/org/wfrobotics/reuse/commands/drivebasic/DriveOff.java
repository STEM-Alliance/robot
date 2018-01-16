package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;

import edu.wpi.first.wpilibj.command.Command;

/** Safety command for drivetrain. Toggle or cancel to quit **/
public class DriveOff extends Command
{
    HerdLogger log = new HerdLogger(DriveOff.class);
    DriveService<?> driveHelper;
    SwerveSignal s = new SwerveSignal(new HerdVector(0, 0));

    public DriveOff(DriveService<?> helper)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
    }

    protected void initialize()
    {
        log.info("Drive Mode", "Off");
    }

    protected void execute()
    {
        log.info("Drive Cmd", s.velocity);
        driveHelper.getDrive().driveBasic(new HerdVector(0, 0));
    }

    protected boolean isFinished()
    {
        return false;
    }
}
