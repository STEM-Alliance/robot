package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

/** Set the jaw solenoids open or closed to nom nom nom dat cube */
public class JawsSet extends Command
{
    private final Intake intake = Intake.getInstance();
    private final boolean wantOpen;

    public JawsSet(boolean open, double timeout, boolean blockIntake)
    {
        if (blockIntake)  // Don't allow SmartIntake to override this autonomous command
        {
            requires(intake);
        }
        wantOpen = open;
        setTimeout(timeout);
    }

    protected void initialize()
    {
        intake.setJaws(wantOpen);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
