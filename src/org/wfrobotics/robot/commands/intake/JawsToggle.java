package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.InstantCommand;

/** Set the solenoids to the opposite state, repeated-buttonsmashing-safe */
public class JawsToggle extends InstantCommand
{
    protected void initialize()
    {
        final IntakeSubsystem intake = IntakeSubsystem.getInstance();
        intake.setHorizontal(!intake.getHorizontal());
    }
}
