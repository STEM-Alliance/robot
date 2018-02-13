package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/** Set the jaw solenoids open or closed to nom nom nom dat cube */
public class JawsSet extends InstantCommand
{
    private final boolean wantOpen;

    public JawsSet(boolean open)
    {
        wantOpen = open;
    }

    protected void initialize()
    {
        Robot.intakeSubsystem.setHorizontal(wantOpen);  // TODO rename to setNom()?
    }
}
