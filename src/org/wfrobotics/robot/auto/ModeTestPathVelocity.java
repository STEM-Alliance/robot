package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.DrivePathVelocity;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeTestPathVelocity extends CommandGroup
{
    public ModeTestPathVelocity()
    {
        this.addSequential(new DrivePathVelocity(12.0 * 17.0, 12.0 * 0.0));
        this.addSequential(new WaitCommand(2.0));
    }
}
