package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.swerve.TurnToInViewTarget;
import org.wfrobotics.robot.commands.VisionModeCamera2;
import org.wfrobotics.robot.commands.VisionModeDefault;

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
