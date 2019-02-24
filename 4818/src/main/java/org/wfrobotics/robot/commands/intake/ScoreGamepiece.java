package org.wfrobotics.robot.commands.intake;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class ScoreGamepiece extends ConditionalCommand
{
    public ScoreGamepiece()
    {
        super(new CargoOut(0.75), new ScoreHatchAndBackUp());
    }

    protected boolean condition()
    {
        return false;  // TODO !Intake.HasHatch()
    }
}