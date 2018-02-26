package org.wfrobotics.robot.commands.drive;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.subsystems.tank.TankSubsystem;
import org.wfrobotics.robot.Robot;

public class DriveCheesy extends DriveCommand
{
    private final TankSubsystem drive;
    private static final CheesyDriveHelper helper = new CheesyDriveHelper();

    public DriveCheesy()
    {
        drive = (TankSubsystem) Robot.driveService.getSubsystem();
        requires(drive);
    }

    protected void execute()
    {
        final DriveSignal s = helper.cheesyDrive(Robot.controls.getThrottle(), Robot.controls.getTurn(), Robot.controls.getDriveQuickTurn(), false);
        Robot.driveService.driveDifferential(s.getLeft(), s.getRight());
    }

    protected boolean isFinished()
    {
        return false;
    }
}
