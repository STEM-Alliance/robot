package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeOpenLoop extends Command
{
    private final Intake intake = Intake.getInstance();
    private final Wrist wrist = Wrist.getInstance();
    private final IO io = IO.getInstance();

    public IntakeOpenLoop()
    {
        requires(intake);
    }

    protected void execute()
    {
        final double speed = io.getIntakeStick();

        if (wrist.isCloserToCargoModeThanHatchMode())
        {
            intake.setCargoSpeed(speed);
            intake.setHatchSpeed(0.0);
        }
        else  // Hatch mode
        {
            intake.setCargoSpeed(0.0);
            intake.setHatchSpeed(speed);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
