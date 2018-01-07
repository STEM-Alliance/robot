package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveMecanum extends Command {

    public DriveMecanum()
    {
        requires(Robot.mecanumDriveSubsystem);
    }

    protected void execute()
    {
        Robot.mecanumDriveSubsystem.setSpeed(
                Robot.controls.mecanumIO.getVector(),
                Robot.controls.mecanumIO.getRotation());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return false;
    }

}
