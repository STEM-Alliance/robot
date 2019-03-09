package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.commands.wrapper.AutoMode;

public class TestAuto extends AutoMode
{
	public TestAuto()
	{
		addSequential(new DriveDistance(12));
		addSequential(new DriveDistance(-12));
		addSequential(new TurnToHeading(0));
	}
}