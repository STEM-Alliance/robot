package org.wfrobotics.robot.config;

import java.util.function.Supplier;

import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.DriveOff;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.auto.AutoSide;
import org.wfrobotics.robot.auto.AutoSwitchCenter;
import org.wfrobotics.robot.auto.ModeCenter;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Autonomous
{
    public static enum POSITION {RIGHT, CENTER, LEFT};

    public static class AutoMode
    {
        public final String text;
        public final Supplier<Command> maker;
        public final double gyroOffset;

        public AutoMode(String chooserText, Supplier<Command> mode, double gyro)
        {
            text = chooserText;
            maker = mode;
            gyroOffset = gyro;
        }

        public static void initChooser()
        {
            AutoMode[] modes = AutoMode.getOptions(0, POSITION.CENTER);
            autoCommands = new SendableChooser<AutoMode>();

            autoCommands.addDefault(modes[0].text, modes[0]);
            for(int index = 1; index < modes.length; index++)
            {
                autoCommands.addObject(modes[index].text, modes[index]);
            }
        }

        /** FIRST Power Up - Top level autonomous modes **/
        public static AutoMode[] getOptions(int delay, POSITION location)
        {
            return new AutoMode[] {
                new AutoMode("Auto None", () -> new DriveOff(), 0.0),
                new AutoMode("Center Switch", () -> new AutoSwitchCenter(), 0.0),
                new AutoMode("New Center Path", () -> new ModeCenter(), 0.0),
                new AutoMode("Side", () -> new AutoSide(location), 0.0),
                new AutoMode("Auto Cross Line", () -> new DriveDistance(12 * 22 + 0), 0.0),
            };
        }
    }

    private static class StartingPosition
    {
        final POSITION location;

        public StartingPosition(POSITION locationOnField)
        {
            location = locationOnField;
        }

        public static void initChooser()
        {
            autoPosition = new SendableChooser<StartingPosition>();
            autoPosition.addDefault("Right", new StartingPosition(POSITION.RIGHT));
            autoPosition.addObject("Center", new StartingPosition(POSITION.CENTER));
            autoPosition.addObject("Left", new StartingPosition(POSITION.LEFT));
        }

        public POSITION get()
        {
            return location;
        }
    }

    private static class Delay
    {
        private final int time;

        public Delay(int seconds)
        {
            time = seconds;
        }

        public static void initChooser()
        {
            int def = 0;
            int[] additionalOptions = {1, 2, 3, 4, 5};
            autoDelay = new SendableChooser<Delay>();

            autoDelay.addDefault(String.valueOf(def), new Delay(def));
            for (int index = 0; index < additionalOptions.length; index++)
            {
                autoDelay.addObject(String.valueOf(additionalOptions[index]), new Delay(additionalOptions[index]));
            }
        }
    }

    private static SendableChooser<AutoMode> autoCommands;
    private static SendableChooser<StartingPosition> autoPosition;
    private static SendableChooser<Delay> autoDelay;

    public static void setupSelection()
    {
        AutoMode.initChooser();
        StartingPosition.initChooser();
        Delay.initChooser();
    }

    /** Grabs the selected 'Auto Mode' from SmartDashboard, sets up gyro, returns the command to run in autonomous */
    public static Command getConfiguredCommand()
    {
        String selected = autoCommands.getSelected().text;
        StartingPosition sp = autoPosition.getSelected();
        int delay = autoDelay.getSelected().time;
        AutoMode[] modes = AutoMode.getOptions(delay, sp.get());
        int choice = 0;

        for (int index = 0; index < modes.length; index++)
        {
            if (modes[index].text.equals(selected))
            {
                choice = index;
                break;
            }
        }

        //        TankSubsystem.getInstance().zeroGyro(modes[choice].gyroOffset);
        RobotState.getInstance().updateRobotVelocity(0.0, modes[choice].gyroOffset);

        return modes[choice].maker.get();
    }
}