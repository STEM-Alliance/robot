package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.wrapper.AutoMode;
import org.wfrobotics.reuse.config.AutoRunner;
import org.wfrobotics.reuse.config.AutoRunner.ModeSafetyOff;
import org.wfrobotics.reuse.config.AutoRunner.Selection;
import org.wfrobotics.robot.auto.ModeCenter;
import org.wfrobotics.robot.auto.ModeOppisitScalse;
import org.wfrobotics.robot.auto.ModeScale;
import org.wfrobotics.robot.auto.ModeSide;

import edu.wpi.first.wpilibj.command.CommandGroup;

/** Configuration of {@link AutoMode} can be run in autonomous mode*/
public abstract class Auto
{
    private static class ModeSelection extends Selection<CommandGroup>
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
    }

    /** Let {@link AutoRunner} know which {@link AutoMode}s to choose from */
    public static void registerModes()
    {
        AutoRunner.getInstance().register(new ModeSelection());
    }
}
