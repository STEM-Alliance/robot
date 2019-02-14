package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.reuse.commands.drive.DriveOpenLoop;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class PopHatchAndBackUp extends CommandGroup
{
    public PopHatchAndBackUp()
    {
        this.addParallel(new DriveOpenLoop(-0.1));
        this.addSequential(new PopHatch());
    }
}
