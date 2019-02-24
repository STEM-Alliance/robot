package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.reuse.commands.drive.DriveOpenLoop;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ScoreHatchAndBackUp extends CommandGroup
{
    public ScoreHatchAndBackUp()
    {
        this.addParallel(new DriveOpenLoop(-0.1));
        this.addSequential(new ScoreHatch());
    }
}
