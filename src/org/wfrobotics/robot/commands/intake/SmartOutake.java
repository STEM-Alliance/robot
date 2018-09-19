package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class SmartOutake extends Command
{
    protected final Intake intake = Intake.getInstance();

    public SmartOutake()
    {
        requires(intake);
        setTimeout(.5);
    }

    protected void execute()
    {
        intake.setMotors(0.30);
        if (timeSinceInitialized() > .2)
        {
            intake.setJaws(true);
        }
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
