package org.wfrobotics.robot.path;

import org.wfrobotics.reuse.commands.SwitchChoice;
import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.DrivePath;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.utilities.MatchState2018.Side;
import org.wfrobotics.robot.auto.IntakeSet;
import org.wfrobotics.robot.commands.wrist.WristToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ModeCenter extends CommandGroup
{
    public ModeCenter()
    {
        addParallel(new WristToHeight(1.0));
        addSequential(new SwitchChoice(Side.Right, new DrivePath("CenterRight"), new DrivePath("CenterLeft")));
        addSequential(new IntakeSet(1.0, 0.5, true));  // Yes, 1.0 outtake is good here
        addSequential(new DriveDistance(-12.0 * 1.0));
        addParallel(new SwitchChoice(Side.Right, new TurnToHeading(-90.0, 1.5), new TurnToHeading(90.0, 1.5)));
    }
}
