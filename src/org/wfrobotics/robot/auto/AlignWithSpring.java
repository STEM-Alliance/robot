package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.swerve.StrafeToInViewTarget;
import org.wfrobotics.reuse.commands.drive.swerve.TurnToInViewTarget;
import org.wfrobotics.robot.commands.VisionModeDefault;
import org.wfrobotics.robot.commands.VisionModeGear;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AlignWithSpring extends CommandGroup
{
    public AlignWithSpring()
    {
        addSequential(new VisionModeGear());
        addSequential(new StrafeToInViewTarget(.6, .2));
        addSequential(new TurnToInViewTarget(.125));
        addSequential(new VisionModeDefault());
    }
}
