package org.wfrobotics.robot.driveoi;

import java.util.ArrayList;

import org.wfrobotics.reuse.commands.driveconfig.FieldRelativeToggle;
import org.wfrobotics.reuse.commands.driveconfig.GyroZero;
import org.wfrobotics.reuse.commands.driveconfig.ShiftToggle;
import org.wfrobotics.reuse.commands.holonomic.DriveCrawl;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.Xbox.BUTTON;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.config.Drive;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

// TODO Button for fast turning (rotate priority over velocity)
// TODO Deadband move to subsystem so we can scale to range at PID?

public class Swerve
{
    // TODO can these be common among drive types?
    // TODO what can move to reuse, maybe with config separated out?
    public interface SwerveIO
    {
        public HerdVector getVelocity();
        public double getRotation();
        public HerdVector getCrawl();
        public boolean getGyroZeroRequested();
    }

    public static class SwerveXbox implements SwerveIO
    {
        private static final double DEADBAND = 0.2;

        protected final Xbox driver;
        protected final Xbox operator;
        private final ArrayList<Button> buttons = new ArrayList<Button>();  // Keep buttons instantiated

        public SwerveXbox(Xbox driver, Xbox operator)
        {
            this.driver = driver;
            this.operator = operator;
            buttons.add(ButtonFactory.makeAnyDpadButton(driver, TRIGGER.WHILE_HELD, new DriveCrawl()));
            buttons.add(ButtonFactory.makeButton(driver, BUTTON.LB, TRIGGER.WHEN_PRESSED, new ShiftToggle()));
            buttons.add(ButtonFactory.makeButton(driver, BUTTON.BACK, TRIGGER.WHEN_PRESSED, new FieldRelativeToggle()));
            buttons.add(ButtonFactory.makeButton(driver, BUTTON.START, TRIGGER.WHEN_PRESSED, new GyroZero()));
        }

        public HerdVector getVelocity()
        {
            HerdVector v = driver.getVector(Hand.kLeft);

            if (v.getMag() < DEADBAND)
            {
                v.scale(0);
            }

            return v;
        }

        public double getRotation()
        {
            double w = operator.getAxis(Xbox.AXIS.RIGHT_X);

            if (Math.abs(w) < DEADBAND)
            {
                w = 0;
            }

            if (Drive.OPERATOR_ROTATE && w != 0)
            {
                return w;
            }

            w = driver.getAxis(Xbox.AXIS.RIGHT_X);

            if (Math.abs(w) < DEADBAND)
            {
                w = 0;
            }

            return w;
        }

        public HerdVector getCrawl()
        {
            double speed = driver.getTrigger(Hand.kLeft);
            double angle = driver.getDpad();
            return new HerdVector(speed, angle);
        }

        public boolean getGyroZeroRequested()
        {
            return driver.getButtonPressed(BUTTON.START);
        }
    }

    public static class SwerveJoyStick implements SwerveIO
    {
        private static final double DEADBAND = 0.2;

        private final Joystick driver;
        private final Xbox operator;
        Button buttonDriveShift;
        Button buttonDriveFieldRelative;
        Button buttonDriveSetGyro;

        public SwerveJoyStick(Joystick driver, Xbox operator)
        {
            this.driver = driver;
            this.operator = operator;
            buttonDriveFieldRelative= new JoystickButton(driver, 8);
            buttonDriveSetGyro = new JoystickButton(driver, 9);
            buttonDriveShift= new JoystickButton(driver, 1);

            buttonDriveShift.whenPressed(new ShiftToggle());
            buttonDriveFieldRelative.whenPressed(new FieldRelativeToggle());
            buttonDriveSetGyro.whenPressed(new GyroZero());
        }

        public HerdVector getVelocity()
        {
            double x = driver.getX();
            double y = driver.getY();
            HerdVector v = new HerdVector(Math.sqrt(x * x + y * y), Math.atan2(x, y) * 180 / Math.PI);

            if (v.getMag() < DEADBAND)
            {
                v.scale(0);
            }

            if (Drive.OPERATOR_ROTATE)
            {
                v.rotate(-operator.getX(Hand.kRight));
            }

            return v;
        }

        public double getRotation()
        {
            double r = operator.getAxis(Xbox.AXIS.LEFT_X);

            if (Math.abs(r) < DEADBAND)
            {
                r = 0;
            }
            return r;
        }

        public HerdVector getCrawl()
        {
            return new HerdVector(driver.getZ(), 0);
        }

        public boolean getGyroZeroRequested()
        {
            return false;
        }
    }
}