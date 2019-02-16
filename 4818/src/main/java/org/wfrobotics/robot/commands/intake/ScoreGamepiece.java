package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class ScoreGamepiece extends ConditionalCommand
{
    private final Wrist wrist = Wrist.getInstance();

    public ScoreGamepiece()
    {
        super(new CargoOut(0.75), new PopHatchAndBackUp());
    }

    protected boolean condition()
    {
        return false;//wrist.isCloserToCargoModeThanHatchMode();
    }
}