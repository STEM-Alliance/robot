package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.wrapper.AutoMode;
import org.wfrobotics.reuse.config.AutoFactory.DelaySelect;
import org.wfrobotics.reuse.config.AutoFactory.ModeSafetyOff;
import org.wfrobotics.reuse.config.AutoFactory.ModeSelectBase;

/** Configuration of {@link AutoMode} can be run in autonomous mode*/
public abstract class Auto
{
    public static ModeSelect modes = new ModeSelect();
    public static DelaySelect delays = new DelaySelect();

    public static class ModeSelect extends ModeSelectBase
    {
        protected AutoMode[] options()
        {
            return new AutoMode[] {
                new ModeSafetyOff(),
            };
        }
    }
}