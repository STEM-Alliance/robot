package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.wrapper.AutoMode;
import org.wfrobotics.reuse.config.AutoFactory.DelaySelect;
import org.wfrobotics.reuse.config.AutoFactory.ModeSafetyOff;
import org.wfrobotics.reuse.config.AutoFactory.ModeSelectBase;
import org.wfrobotics.reuse.config.AutoSelection;

/** Configuration of {@link AutoMode} can be run in autonomous mode*/
public final class Auto
{
    public static ModeSelect modes = new ModeSelect();
    public static DelaySelect delays = new DelaySelect();
    public static PositionSelect positions = new PositionSelect();

    private Auto() { /** No instances */ };

    public static class ModeSelect extends ModeSelectBase
    {
        protected AutoMode[] options()
        {
            return new AutoMode[] {
                new ModeSafetyOff(),
            };
        }
    }

    public static enum POSITION { LEFT, CENTER, RIGHT, };

    public static class PositionSelect extends AutoSelection<POSITION>
    {
        protected POSITION[] options()
        {
            return new POSITION[] {POSITION.LEFT, POSITION.CENTER, POSITION.RIGHT};
        }

        protected void apply() { }
    }
}