package org.wfrobotics.robot.config;

import java.util.function.Supplier;

import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.auto.print.AutoLine;
import org.wfrobotics.robot.auto.print.AutoScore7BoxesInExchangeWOW;
import org.wfrobotics.robot.auto.print.AutoStraitSwitch;
import org.wfrobotics.robot.auto.print.AutoTurnSwitch;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// TODO Recursively log mode/profile of composing commands with newlines, could ask each to print their toString?

/** Organizes autonomous modes supported by Robot **/
public class Autonomous
{
    private static enum POSITION_ROTARY {RIGHT, CENTER, LEFT};
    private static SendableChooser<AutoMode> autoCommands;

    /** FIRST Power Up - Top level autonomous modes **/
    public static AutoMode[] makeModes()
    {
        // TODO Intelligently use the starting position from panel board dial? Which commands care where we start the robot?

        return new AutoMode[] {
            /* example command options
             * new AutoMode("Auto None", () -> new DriveOff(), 0),
             * new AutoMode("Auto Forward (LOW GEAR)", () -> new AutoDrive(new HerdVector(DRIVE_SPEED, 0), TIME_DRIVE_MODE), 0),
             * new AutoMode("Auto Forward (HIGH GEAR)", () -> new AutoDrive(new HerdVector(DRIVE_SPEED * .75, 0), TIME_DRIVE_MODE * .75), 0),
             */
            // TODO Delete this if cross line command works?
            // TODO Drive distance, cross line - must have by first regional
            // TODO Score on scale - PRIORITIZE MAKING THIS AMAZING!!!
            // TODO Score on scale then do some extra stuff(s)
            // TODO Score switch first
            new AutoMode("Auto Line", () -> new AutoLine(), 0),
            new AutoMode("Auto Strait", () -> new AutoStraitSwitch(), 0),
            new AutoMode("Auto Left", () -> new AutoTurnSwitch(true), 0),
            new AutoMode("Auto Right", () -> new AutoTurnSwitch(false), 0),
            new AutoMode("Auto Exchange", () -> new AutoScore7BoxesInExchangeWOW(), 0),
        };
    }
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

    /** Grabs the selected 'Auto Mode' from SmartDashboard, sets up gyro, returns the command to run in autonomous */
    protected static Command setupSelectedMode()
    {
        String selected = autoCommands.getSelected().text;//SmartDashboard.getString("Auto Mode", "Auto None");
        AutoMode[] modes = makeModes();
        int choice = 0;

        for (int index = 0; index < modes.length; index++)
        {
            if (modes[index].text.equals(selected)) { choice = index; break; }
        }

        Gyro.getInstance().zeroYaw(modes[choice].gyroOffset);
        RobotState.getInstance().updateRobotHeading(modes[choice].gyroOffset);

        return modes[choice].maker.get();
    }

    public static void setupSendableChooser() {
        //        SendableChooser<AutoMode> autoCommands = new SendableChooser<AutoMode>();
        autoCommands = new SendableChooser<AutoMode>();
        for(AutoMode auto: makeModes()) {
            autoCommands.addObject(auto.text, auto);
        }
        autoCommands.addDefault("Auto Line", new AutoMode("Auto Line", () -> new AutoLine(), 0));
        SmartDashboard.putData("Auto Mode", autoCommands);
    }

    /** Grabs the selected 'Auto Mode' from SmartDashboard, sets up gyro, returns the command to run in autonomous */
    public static Command setupAndReturnSelectedMode()
    {
        SmartDashboard.getString("Position", "test");
        getRotaryStartingPosition();
        return new Autonomous().setupSelectedMode();  // Static getter seemed cleaner in Robot
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