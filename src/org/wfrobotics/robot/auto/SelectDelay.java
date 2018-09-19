package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.config.AutoSelection;
import org.wfrobotics.robot.subsystems.AutoRunner;

public class SelectDelay extends AutoSelection<AutoRunner>
{
    public SelectDelay()
    {
        super(AutoRunner.getInstance());
    }

    protected void onSelected()
    {
        subsystem.nextDelay();
    }
}
