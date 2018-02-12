package org.wfrobotics.robot.commands.intake;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

/** Automatically pull in cube, yet allow manual override of any intake hardware at any time */
public class CyborgIntake extends ConditionalCommand
{
    public CyborgIntake()
    {
        super(new IntakeManual(), new SmartIntake());
    }

    protected boolean condition()
    {
        return IntakeManual.wantsManualIntake();
    }
}
