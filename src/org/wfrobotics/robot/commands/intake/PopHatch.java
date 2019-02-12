package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class PopHatch extends InstantCommand
{
    private final Intake intake = Intake.getInstance();
    private final boolean desired;

    public PopHatch(boolean out)
    {
        requires(intake);
        desired = out;
    }

    protected void initialize()
    {
        intake.setPoppers(desired);
    }
}
