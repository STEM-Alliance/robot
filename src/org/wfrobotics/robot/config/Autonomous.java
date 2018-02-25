package org.wfrobotics.robot.config;

import java.util.function.Supplier;

import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.DriveOff;
import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.auto.PrintCommand;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Organizes autonomous modes supported by Robot **/
public class Autonomous
{
    /** Defines everything needed for a singular autonomous mode the robot may run */
    protected static class AutoMode
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
    }

    private static enum POSITION_ROTARY {RIGHT, CENTER, LEFT};
    private static SendableChooser<AutoMode> autoCommands;
    private static SendableChooser<POSITION_ROTARY> autoLocation;

    /** FIRST Power Up - Top level autonomous modes **/
    public static AutoMode[] makeModes(boolean mirror)
    {
        return new AutoMode[] {
            new AutoMode("Auto Zero", () -> new PrintCommand("0"), 0.0),
            new AutoMode("Auto One", () -> new PrintCommand("1"), 0.0),
            new AutoMode("Auto None", () -> new DriveOff(), 0.0),
            new AutoMode("Auto Cross Line", () -> new DriveDistance(12 * 22 + 0), 0.0),
        };
    }

    /** Grabs the selected 'Auto Mode' from SmartDashboard, sets up gyro, returns the command to run in autonomous */
    public static Command setupAndReturnSelectedMode()
    {
        //        SmartDashboard.getString("Position", "test");
        //        getRotaryStartingPosition();
        return setupSelectedMode();
    }

    /** Grabs the selected 'Auto Mode' from SmartDashboard, sets up gyro, returns the command to run in autonomous */
    protected static Command setupSelectedMode()
    {
        String selected = autoCommands.getSelected().text;
        boolean mirror = autoLocation.getSelected() == POSITION_ROTARY.LEFT;
        AutoMode[] modes = makeModes(mirror);
        int choice = 0;

        for (int index = 0; index < modes.length; index++)
        {
            if (modes[index].text.equals(selected)) { choice = index; break; }
        }

        Gyro.getInstance().zeroYaw(modes[choice].gyroOffset);
        RobotState.getInstance().updateRobotHeading(modes[choice].gyroOffset);

        return modes[choice].maker.get();
    }

    public static void setupSendableChooser()
    {
        autoCommands = new SendableChooser<AutoMode>();
        AutoMode[] modes = makeModes(false);
        for(int index = 0; index < modes.length; index++)
        {
            autoCommands.addObject(modes[index].text, modes[index]);
        }
        autoCommands.addDefault(modes[0].text, modes[0]);
        SmartDashboard.putData("Auto", autoCommands);

        autoLocation = new SendableChooser<POSITION_ROTARY>();
        autoLocation.addObject("Right", POSITION_ROTARY.RIGHT);
        autoLocation.addObject("Center", POSITION_ROTARY.CENTER);
        autoLocation.addObject("Left", POSITION_ROTARY.LEFT);
        autoLocation.addDefault("Right", POSITION_ROTARY.RIGHT);
        SmartDashboard.putData("Auto Location", autoLocation);
    }

    /** Get the starting position on field for when we switch to autonomous mode */
    protected static POSITION_ROTARY getRotaryStartingPosition()
    {
        HerdLogger log = new HerdLogger(POSITION_ROTARY.class);
        int dial = Robot.controls.getAutonomousSide();
        POSITION_ROTARY position;

        if (dial == 1)
        {
            position = POSITION_ROTARY.RIGHT;
            log.info("Autonomous Starting Position", "RIGHT");
        }
        else if (dial == 7)
        {
            position = POSITION_ROTARY.LEFT;
            log.info("Autonomous Starting Position", "LEFT");
        }
        else if (dial == 0)
        {
            position = POSITION_ROTARY.CENTER;
            log.info("Autonomous Starting Position", "CENTER");
        }
        else
        {
            position = POSITION_ROTARY.CENTER;
            log.warning("Autonomous Starting Position", "(defaulting to) CENTER");
        }
        SmartDashboard.getString("Position", "right");

        return position;
    }
}