package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSwitchSide extends CommandGroup
{
    /**
     * @boolean true is 90 (Going to the Right)
     *          false is -90 (going to the Left)
     */

    public AutoSwitchSide(boolean direction)
    {
        addSequential(new DriveDistance(135));
        addSequential(new TurnToHeading((direction) ? 90 : -90, 0.2));
        addSequential(new DriveDistance(18));
    }

    public AutoSwitchSide(String name)
    {
        super(name);
    }
}
