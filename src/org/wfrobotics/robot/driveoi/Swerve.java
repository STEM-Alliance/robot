package org.wfrobotics.robot.driveoi;

import java.util.ArrayList;

import org.wfrobotics.reuse.commands.driveconfig.FieldRelativeToggle;
import org.wfrobotics.reuse.commands.driveconfig.GyroZero;
import org.wfrobotics.reuse.commands.driveconfig.ShiftToggle;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.Xbox.BUTTON;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.commands.Conveyor;
import org.wfrobotics.robot.commands.Rev;
import org.wfrobotics.robot.commands.Shoot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class Swerve
{
    public interface SwerveIO
    {
        public double getCrawlSpeed();
        public int getDpad();
        public boolean getGyroZero();
        public double getHaloDrive_Rotation();
        public HerdVector getHaloDrive_Velocity();
        public double getFusionDrive_Rotation();
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
            buttons.add(ButtonFactory.makeButton(driver, BUTTON.LB, TRIGGER.WHEN_PRESSED, new ShiftToggle()));
            buttons.add(ButtonFactory.makeButton(driver, BUTTON.BACK, TRIGGER.WHEN_PRESSED, new FieldRelativeToggle()));
            buttons.add(ButtonFactory.makeButton(driver, BUTTON.START, TRIGGER.WHEN_PRESSED, new GyroZero()));
        }

        /**
         * Get the Rotation value of the joystick for Halo Drive
         * @return The Rotation value of the joystick.
         */
        public double getHaloDrive_Rotation()
        {
            double value = driver.getAxis(Xbox.AXIS.RIGHT_X);

            if (Math.abs(value) < DEADBAND)
            {
                value = 0;
            }
            return value;
        }

        /**
         * Get the {@link HerdVector} (mag & angle) of the velocity joystick for Halo Drive
         * @return The vector of the joystick.
         */
        public HerdVector getHaloDrive_Velocity()
        {
            HerdVector v = driver.getVector(Hand.kLeft);

            if (v.getMag() < DEADBAND)
            {
                v.scale(0);
            }
            return v;
        }

        public double getCrawlSpeed()
        {
            return driver.getTrigger(Hand.kLeft);
        }

        public int getDpad()
        {
            return driver.getDpad();
        }

        public double getFusionDrive_Rotation()
        {
            return operator.getX(Hand.kRight);
        }

        public boolean getGyroZero()
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
        Button buttonDriveSmartShoot;
        Button buttonDriveDumbShoot;
        Button buttonDriveFieldRelative;
        Button buttonDriveSetGyro;

        public SwerveJoyStick(Joystick driver, Xbox operator)
        {
            this.driver = driver;
            this.operator = operator;
            buttonDriveSmartShoot = new JoystickButton(driver, 6);
            buttonDriveDumbShoot = new JoystickButton(driver, 7);
            buttonDriveFieldRelative= new JoystickButton(driver, 8);
            buttonDriveSetGyro = new JoystickButton(driver, 9);

            buttonDriveShift= new JoystickButton(driver, 1);

            buttonDriveDumbShoot.whileHeld(new Rev(Rev.MODE.SHOOT));
            buttonDriveSmartShoot.whileHeld(new Shoot(Conveyor.MODE.CONTINUOUS));
            buttonDriveShift.whenPressed(new ShiftToggle());
            buttonDriveFieldRelative.whenPressed(new FieldRelativeToggle());
            buttonDriveSetGyro.whenPressed(new GyroZero());
        }

        public double getHaloDrive_Rotation()
        {
            double r = operator.getAxis(Xbox.AXIS.LEFT_X);

            if (Math.abs(r) < DEADBAND)
            {
                r = 0;
            }
            return r;
        }

        public HerdVector getHaloDrive_Velocity()
        {
            double x = driver.getX();
            double y = driver.getY();
            HerdVector v = new HerdVector(Math.sqrt(x * x + y * y), Math.atan2(y, x) * 180 / Math.PI);

            if (v.getMag() < DEADBAND)
            {
                v.scale(0);
            }
            return v;
        }

        public double getCrawlSpeed()
        {
            return driver.getZ();
        }

        public int getDpad()
        {
            return 0;
        }

        public double getFusionDrive_Rotation()
        {
            return operator.getX(Hand.kRight);
        }

        public boolean getGyroZero()
        {
            return false;
        }
    }
}