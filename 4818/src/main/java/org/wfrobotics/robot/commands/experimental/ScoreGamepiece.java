package org.wfrobotics.robot.commands.experimental;

import org.wfrobotics.robot.commands.intake.CargoOut;
import org.wfrobotics.robot.commands.intake.ScoreHatch;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class ScoreGamepiece extends ConditionalCommand
{
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public ScoreGamepiece()
    {
        super(new CargoOut(0.75), new ScoreHatch());
    }

    protected boolean condition()
    {
        return superStructure.getHasCargo();
    }
}