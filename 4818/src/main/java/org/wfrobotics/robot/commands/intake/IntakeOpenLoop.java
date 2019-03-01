package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeOpenLoop extends Command
{
    private final Intake intake = Intake.getInstance();
    private final IO io = IO.getInstance();

    public IntakeOpenLoop()
    {
        requires(intake);
    }

    protected void execute()
    {
        // final double speed = io.getIntakeStick();
        final double speed = 0.0;
        intake.setCargoSpeed(speed);
        if (intake.hasHatch())
        {
            intake.setGrabber(true);
        }
        else { intake.setGrabber(false); }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
