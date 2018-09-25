package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.wrapper.AutoMode;
import org.wfrobotics.reuse.config.AutoFactory;
import org.wfrobotics.reuse.config.AutoFactory.ModeSafetyOff;
import org.wfrobotics.reuse.config.Selection;
import org.wfrobotics.robot.auto.ModeCenter;
import org.wfrobotics.robot.auto.ModeOppisitScalse;
import org.wfrobotics.robot.auto.ModeScale;
import org.wfrobotics.robot.auto.ModeSide;

import edu.wpi.first.wpilibj.command.CommandGroup;

/** Configuration of {@link AutoMode} can be run in autonomous mode*/
public abstract class Auto
{
    public static ModeSelection modes = new ModeSelection();
    public static DelaySelection delays = new DelaySelection();
    public static AutoFactory<Autonomous> factory = new AutoFactory<Autonomous>(new Autonomous());
    public static PositionSelection positions = new PositionSelection();

    public static class Autonomous extends AutoMode
    {
        public POSITION startLocation;

        @SuppressWarnings("unchecked")
        public Autonomous getNewInstance()
        {
            return new Autonomous();
        }

        public void setPosition(POSITION p)
        {
            startLocation = p;
        }

        public String toString()
        {
            return String.format("%s, %s", super.toString(), startLocation.toString());
        }
    }

    private static class ModeSelection extends Selection<Autonomous, CommandGroup>
    {
        protected CommandGroup[] options()
        {
            return new CommandGroup[] {
                new ModeSafetyOff(),
                new ModeScale(),
                new ModeOppisitScalse(),
                new ModeCenter(),
                new ModeSide(),
            };
        }

        protected void apply(Autonomous mode)
        {
            mode.setMode(get());
        }
    }

    public static class DelaySelection extends Selection<Autonomous, Integer>
    {
        protected Integer[] options()
        {
            return new Integer[] {0, 1, 2, 3, 4, 5};
        }

        protected void apply(Autonomous mode)
        {
            mode.setDelay(get());
        }
    }

    public static enum POSITION { LEFT, CENTER, RIGHT, };

    public static class PositionSelection extends Selection<Autonomous, POSITION>
    {
        protected POSITION[] options()
        {
            return new POSITION[] {POSITION.LEFT, POSITION.CENTER, POSITION.RIGHT};
        }

        protected void apply(Autonomous mode)
        {
            mode.setPosition(get());
        }
    }
}
