package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drivebasic.TurnToInViewTarget;
import org.wfrobotics.robot.commands.vision.VisionModeCamera2;
import org.wfrobotics.robot.commands.vision.VisionModeDefault;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionCamera2Turn extends CommandGroup
{
    public VisionCamera2Turn()
    {
        addSequential(new VisionModeCamera2());
        addSequential(new TurnToInViewTarget(.1));
        addSequential(new VisionModeDefault());
    }
}
