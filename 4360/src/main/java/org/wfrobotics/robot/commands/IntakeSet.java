package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class IntakeSet extends InstantCommand
{
	private Intake subsystem = Intake.getInstance();
	private final boolean desired;
	
	public IntakeSet(boolean open)
	{
		this.requires(subsystem);
		desired = open;
	}
	
	@Override
	protected void initialize()
	{
		subsystem.set(desired);
	}
}