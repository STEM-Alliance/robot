package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.swerve.TurnToInViewTarget;
import org.wfrobotics.robot.commands.VisionModeDefault;
import org.wfrobotics.robot.commands.VisionModeShooter;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionShoot extends CommandGroup
{
    public VisionShoot()
    {
        addSequential(new VisionModeShooter());
        addSequential(new TurnToInViewTarget(.1));
        addSequential(new VisionModeDefault());
    }
}
