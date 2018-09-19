package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.config.AutoSelection;
import org.wfrobotics.robot.subsystems.AutoRunner;

public class SelectMode extends AutoSelection<AutoRunner>
{
    public SelectMode()
    {
        super(AutoRunner.getInstance());
    }

    protected void onSelected()
    {
        subsystem.nextMode();
    }
}
