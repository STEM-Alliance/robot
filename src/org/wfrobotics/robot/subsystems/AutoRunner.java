package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.commands.AutoMode;
import org.wfrobotics.reuse.subsystems.AutoRunnerBase;
import org.wfrobotics.robot.auto.ModeCenter;
import org.wfrobotics.robot.auto.ModeOppisitScalse;
import org.wfrobotics.robot.auto.ModeScale;
import org.wfrobotics.robot.auto.ModeSide;

/** Selects, sets up, and runs an autonomous mode */
public class AutoRunner extends AutoRunnerBase
{
    private static class ModeSelection extends Selection<AutoMode>
    {
        protected AutoMode[] options()
        {
            return new AutoMode[] {
                new ModeSafetyOff(),
                new ModeScale(),
                new ModeOppisitScalse(),
                new ModeCenter(),
                new ModeSide(),
            };
        }
    }

    private static AutoRunner instance = null;

    private AutoRunner()
    {
        super(new ModeSelection());
    }

    public static AutoRunner getInstance()
    {
        if (instance == null)
        {
            instance = new AutoRunner();
        }
        return instance;
    }
}
