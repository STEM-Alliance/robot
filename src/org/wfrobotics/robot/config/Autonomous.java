package org.wfrobotics.robot.config;

import java.util.function.Supplier;

import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// TODO Recursively log mode/profile of composing commands with newlines, could ask each to print their toString?

public class Autonomous
{
    static final double DRIVE_SPEED = .6;
    static final double TIME_DRIVE_MODE = 4;

    public enum POSITION_ROTARY {SIDE_A, CENTER, SIDE_B};
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
    }

    private static AutoMode[] makeModes()
    {
        POSITION_ROTARY startingPosition = getRotaryStartingPosition();
        int signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1 : -1; // X driving based on alliance for mirrored field

        return new AutoMode[] {
//                new AutoMode("Auto None", () -> new DriveOff(), 0),
//                new AutoMode("Auto Forward (LOW GEAR)", () -> new AutoDrive(0, DRIVE_SPEED, TIME_DRIVE_MODE), 0),
//                new AutoMode("Auto Forward (HIGH GEAR)", () -> new AutoDrive(0, DRIVE_SPEED * .75, TIME_DRIVE_MODE * .75), 0),
        };
    }

    public static Command setupSelectedMode()
    {
        String selected = SmartDashboard.getString("Auto Mode", "Auto None");
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

    /**
     * Get the starting position on field for when we switch to autonomous mode
     * @return Boiler, Center, or Loading Station side of field
     */
    public static POSITION_ROTARY getRotaryStartingPosition()
    {
        HerdLogger log = new HerdLogger(POSITION_ROTARY.class);
        int dial = Robot.controls.getAutonomousSide();
        Alliance alliance = DriverStation.getInstance().getAlliance();
        POSITION_ROTARY position;

        if (alliance == Alliance.Blue && dial == 7 || alliance == Alliance.Red && dial == 1)
        {
            position = POSITION_ROTARY.SIDE_A;
            log.info("Autonomous Starting Position", "BOILER");
        }
        else if (alliance == Alliance.Blue && dial == 1 || alliance == Alliance.Red && dial == 7)
        {
            position = POSITION_ROTARY.SIDE_B;
            log.info("Autonomous Starting Position", "LOADING STATION");
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
