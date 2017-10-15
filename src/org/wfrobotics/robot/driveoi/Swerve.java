package org.wfrobotics.robot.driveoi;

import org.wfrobotics.reuse.commands.driveconfig.FieldRelativeToggle;
import org.wfrobotics.reuse.commands.driveconfig.GyroZero;
import org.wfrobotics.reuse.commands.driveconfig.ShiftToggle;
import org.wfrobotics.reuse.controller.Panel;
import org.wfrobotics.reuse.controller.Panel.BUTTON;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.XboxButton;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.Utilities;
import org.wfrobotics.robot.commands.Conveyor;
import org.wfrobotics.robot.commands.Rev;
import org.wfrobotics.robot.commands.Shoot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class Swerve
{
    public interface SwerveOI
    {
        public double getHaloDrive_Rotation();
        public HerdVector getHaloDrive_Velocity();
        public double getAngleDrive_Heading();
        public double getAngleDrive_Rotation();
        public HerdVector getAngleDrive_Velocity();
        public double getCrawlSpeed();
        public int getDpad();
        public double[] getPanelKnobs();
        public boolean getPanelSave();
        public double getFusionDrive_Rotation();
    }

    public static class SwerveXBox implements SwerveOI
    {
        private static final double DEADBAND = 0.2;

        private final Xbox driver1;
        private final Xbox driver2;
        private final Panel panel;

        Button buttonDriveShift;
        Button buttonDriveVisionShoot;
        Button buttonDriveSmartShoot;
        Button buttonDriveDumbShoot;
        Button buttonDriveFieldRelative;
        Button buttonDriveSetGyro;

        public SwerveXBox(Xbox driver1, Xbox driver2, Panel panel)
        {
            this.driver1 = driver1;
            this.driver2 = driver2;
            this.panel = panel;

            buttonDriveSmartShoot = new XboxButton(driver1, Xbox.BUTTON.B);
            buttonDriveDumbShoot = new XboxButton(driver1, Xbox.BUTTON.A);
            buttonDriveVisionShoot= new XboxButton(driver1, Xbox.BUTTON.RB);
            buttonDriveShift= new XboxButton(driver1, Xbox.BUTTON.LB);
            buttonDriveFieldRelative= new XboxButton(driver1, Xbox.BUTTON.BACK);
            buttonDriveSetGyro = new XboxButton(driver1, Xbox.BUTTON.START);


            //buttonDriveVisionShoot.toggleWhenPressed(new VisionShoot());
            buttonDriveDumbShoot.whileHeld(new Rev(Rev.MODE.SHOOT));
            buttonDriveSmartShoot.whileHeld(new Shoot(Conveyor.MODE.CONTINUOUS));
            buttonDriveShift.whenPressed(new ShiftToggle());
            buttonDriveFieldRelative.whenPressed(new FieldRelativeToggle());
            buttonDriveSetGyro.whenPressed(new GyroZero());
        }


        /**
         * Get the Rotation value of the joystick for Halo Drive
         * 
         * @return The Rotation value of the joystick.
         */
        public double getHaloDrive_Rotation()
        {
            double value = 0;

            value = driver1.getAxis(Xbox.AXIS.RIGHT_X);

            if (Math.abs(value) < DEADBAND)
            {
                value = 0;
            }
            return value;
        }

        /**
         * Get the {@link HerdVector} (mag & angle) of the velocity joystick for Halo
         * Drive
         * 
         * @return The vector of the joystick.
         */
        public HerdVector getHaloDrive_Velocity()
        {
            HerdVector v = driver1.getVector(Hand.kLeft);

            if (v.getMag() < DEADBAND)
            {
                v.scale(0);
            }

            return v;
        }

        /**
         * Get the heading/angle in degrees for Angle Drive
         * 
         * @return The angle in degrees of the joystick.
         */
        public double getAngleDrive_Heading()
        {
            double angle = -1;

            if (driver1.getMagnitude(Hand.kRight) > 0.65)
            {
                angle = driver1.getAngleDegrees(Hand.kRight);
            }

            return angle;
        }

        /**
         * Get the rotation for Angle Drive
         * 
         * @return The rotation rate in rad/s.
         */
        public double getAngleDrive_Rotation()
        {
            double rotation = 0;
            int dpad = getDpad();

            if (dpad == 90)
            {
                rotation = .75;
            }
            else if (dpad == 270)
            {
                rotation = -.75;
            }
            return rotation;
        }

        /**
         * Get the {@link HerdVector} (mag & angle) of the velocity joystick for Angle
         * Drive
         * 
         * @return The vector of the joystick.
         */
        public HerdVector getAngleDrive_Velocity()
        {
            HerdVector v = driver1.getVector(Hand.kLeft);

            if (v.getMag() < DEADBAND)
            {
                v.scale(0);
            }
            return v;
        }

        public double getCrawlSpeed()
        {
            return driver1.getTrigger(Hand.kLeft);
        }

        public int getDpad()
        {
            return driver1.getDpad();
        }

        public double[] getPanelKnobs()
        {
            return new double[] {
                    panel.getTopDial(Hand.kLeft) * 180.0,
                    panel.getTopDial(Hand.kRight) * 180.0,
                    panel.getBottomDial(Hand.kLeft) * 180.0,
                    panel.getBottomDial(Hand.kRight) * 180.0,
            };
        }

        public boolean getPanelSave()
        {
            return panel.getButton(BUTTON.SWITCH_L) && panel.getButton(BUTTON.SWITCH_R);
        }

        public double getFusionDrive_Rotation()
        {
            return driver2.getX(Hand.kRight);
        }
    }

    public static class SwerveJoyStick implements SwerveOI
    {

        private static final double DEADBAND = 0.2;

        private final Joystick driver1;
        private final Xbox driver2;
        private final Panel panel;

        Button buttonDriveShift;
        Button buttonDriveVisionShoot;
        Button buttonDriveSmartShoot;
        Button buttonDriveDumbShoot;
        Button buttonDriveFieldRelative;
        Button buttonDriveSetGyro;

        public SwerveJoyStick(Joystick driver1, Xbox driver2, Panel panel)
        {
            this.driver1 = driver1;
            this.driver2 = driver2;
            this.panel = panel;

            buttonDriveSmartShoot = new JoystickButton(driver1, 6);
            buttonDriveDumbShoot = new JoystickButton(driver1, 7);
            buttonDriveVisionShoot= new JoystickButton(driver1, 11);
            buttonDriveFieldRelative= new JoystickButton(driver1, 8);
            buttonDriveSetGyro = new JoystickButton(driver1, 9);

            buttonDriveShift= new JoystickButton(driver1, 1);

            //buttonDriveVisionShoot.toggleWhenPressed(new VisionShoot());
            buttonDriveDumbShoot.whileHeld(new Rev(Rev.MODE.SHOOT));
            buttonDriveSmartShoot.whileHeld(new Shoot(Conveyor.MODE.CONTINUOUS));
            buttonDriveShift.whenPressed(new ShiftToggle());
            buttonDriveFieldRelative.whenPressed(new FieldRelativeToggle());
            buttonDriveSetGyro.whenPressed(new GyroZero());
        }

        public double getHaloDrive_Rotation()
        {
            // TODO Auto-generated method stub

            double r = 0;

            r = driver2.getAxis(Xbox.AXIS.LEFT_X);

            if (Math.abs(r) < DEADBAND)
            {
                r = 0;
            }
            return r;
        }

        public HerdVector getHaloDrive_Velocity()
        {
            double x = driver1.getX();
            double y = driver1.getY();
            HerdVector v = new HerdVector(Math.sqrt(x * x + y * y), Math.atan2(y, x) * 180 / Math.PI);

            if (v.getMag() < DEADBAND)
            {
                v.scale(0);
            }

            return v;
        }

        public double getAngleDrive_Heading()
        {
            double angle;
            if (driver1.getDirectionDegrees() > 0.65)
            {
                angle = driver1.getDirectionDegrees();
                return Utilities.wrapToRange(angle + 90, -180, 180);
            }
            return 0;
        }

        public double getAngleDrive_Rotation()
        {
            return 0;
        }

        public HerdVector getAngleDrive_Velocity()
        {
            return null;
        }

        public double getCrawlSpeed()
        {
            return driver1.getZ();
        }

        public int getDpad()
        {
            return 0;
        }

        public double[] getPanelKnobs()
        {
            return new double[] {
                    panel.getTopDial(Hand.kLeft) * 180.0,
                    panel.getTopDial(Hand.kRight) * 180.0,
                    panel.getBottomDial(Hand.kLeft) * 180.0,
                    panel.getBottomDial(Hand.kRight) * 180.0,
            };
        }

        public boolean getPanelSave()
        {
            return panel.getButton(BUTTON.SWITCH_L) && panel.getButton(BUTTON.SWITCH_R);
        }

        public double getFusionDrive_Rotation()
        {
            return driver2.getX(Hand.kRight);
        }
    }
}