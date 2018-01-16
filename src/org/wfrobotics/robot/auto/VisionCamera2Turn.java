package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drivebasic.TurnToInViewTarget;
import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.robot.commands.VisionModeCamera2;
import org.wfrobotics.robot.commands.VisionModeDefault;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionCamera2Turn extends CommandGroup
{
    public VisionCamera2Turn(DriveService<?> helper)
    {
        addSequential(new VisionModeCamera2());
        addSequential(new TurnToInViewTarget(helper, .1));
        addSequential(new VisionModeDefault());
    }
}
