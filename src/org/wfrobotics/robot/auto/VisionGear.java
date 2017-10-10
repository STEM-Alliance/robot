package org.wfrobotics.robot.auto;

import org.wfrobotics.robot.commands.VisionModeDefault;
import org.wfrobotics.robot.commands.VisionModeGear;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionGear extends CommandGroup
{
    final static double HEXAGON_ANGLE = 30;  // All corners are 120 on the interior, therefore the sides we want are 30 degrees past straight ahead

    public VisionGear()
    {
        addSequential(new VisionModeGear());
        addSequential(new VisionGearApproach());
        addSequential(new VisionModeDefault());
    }
}
