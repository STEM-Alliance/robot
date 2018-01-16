package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.subsystems.drive.HolonomicService;
import org.wfrobotics.robot.commands.VisionModeCamera1;
import org.wfrobotics.robot.commands.VisionModeDefault;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionCamera1Approach extends CommandGroup
{
    public VisionCamera1Approach(HolonomicService<?> helper)
    {
        addSequential(new VisionModeCamera1());
        addSequential(new VisionApproach(helper));
        addSequential(new VisionModeDefault());
    }
}
