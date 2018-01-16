package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class ShiftToggle extends InstantCommand
{
    DriveService<?> driveHelper;

    public ShiftToggle(DriveService<?> helper)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
    }

    protected void initialize()
    {
        driveHelper.getDrive().setGear(!RobotState.getInstance().robotGear);
    }
}
