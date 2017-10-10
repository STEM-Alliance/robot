package org.wfrobotics.robot.auto;

import org.wfrobotics.robot.commands.VisionModeDefault;
import org.wfrobotics.robot.commands.VisionModeShooter;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionShoot extends CommandGroup
{
    public VisionShoot()
    {
        addSequential(new VisionModeShooter());
        addSequential(new VisionShootAim());
        addSequential(new VisionModeDefault());
    }
}
