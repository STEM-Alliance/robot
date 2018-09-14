package org.wfrobotics.robot.auto.modes;

import org.wfrobotics.robot.auto.IntakeSet;
import org.wfrobotics.robot.commands.wrist.WristToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ModeCenter extends CommandGroup
{
    public ModeCenter()
    {
        addParallel(new WristToHeight(90.0));
        //        addSequential(new SwitchChoice(Side.Right, new DrivePathPosition("CenterRight"), new DrivePathPosition("CenterLeft")));
        addSequential(new IntakeSet(1.0, 0.5));  // Yes, 1.0 outtake is good here
    }

}
