package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.drivebasic.AutoDrive;
import org.wfrobotics.reuse.commands.drivebasic.DriveOff;
import org.wfrobotics.reuse.utilities.AutoPicker;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

// TODO Recursively log mode/profile of composing commands with newlines, could ask each to print their toString?

/** Organizes autonomous modes supported by Robot **/
public class Autonomous extends AutoPicker
{
    private enum POSITION_ROTARY {RIGHT, CENTER, LEFT};

    private final double DRIVE_SPEED = .6;
    private final double TIME_DRIVE_MODE = 4;

    /** FIRST Power Up - Top level autonomous modes **/
    protected AutoMode[] makeModes()
    {
        // TODO Intelligently use the starting position from panel board dial? Which commands care where we start the robot?

        return new AutoMode[] {
            new AutoMode("Auto None", () -> new DriveOff(), 0),
            // TODO Delete this if cross line command works?
            new AutoMode("Auto Forward (LOW GEAR)", () -> new AutoDrive(new HerdVector(DRIVE_SPEED, 0), TIME_DRIVE_MODE), 0),
            new AutoMode("Auto Forward (HIGH GEAR)", () -> new AutoDrive(new HerdVector(DRIVE_SPEED * .75, 0), TIME_DRIVE_MODE * .75), 0),
            // TODO Drive distance, cross line - must have by first regional
            // TODO Score on scale - PRIORITIZE MAKING THIS AMAZING!!!
            // TODO Score on scale then do some extra stuff(s)
            // TODO Score switch first
        };
    }

    /** Grabs the selected 'Auto Mode' from SmartDashboard, sets up gyro, returns the command to run in autonomous */
    public static Command setupAndReturnSelectedMode()
    {
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

        return position;
    }
}
