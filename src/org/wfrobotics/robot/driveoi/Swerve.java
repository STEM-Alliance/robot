package org.wfrobotics.robot.driveoi;

import org.wfrobotics.Vector;
import org.wfrobotics.controller.Panel;
import org.wfrobotics.controller.Xbox;
import org.wfrobotics.controller.Panel.BUTTON;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Swerve
{
    public interface SwerveOI
    {
        public double getHaloDrive_Rotation();
        public Vector getHaloDrive_Velocity();
        public double getAngleDrive_Heading();
        public double getAngleDrive_Rotation();
        public Vector getAngleDrive_Velocity();
        public double getCrawlSpeed();
        public int getDpad();
        public double[] getPanelKnobs();
        public boolean getPanelSave();
        public double getFusionDrive_Rotation();
    }
    
    public class SwerveXBox implements SwerveOI
    {
        private static final double DEADBAND = 0.2;
        
        private final Xbox driver1;
        private final Xbox driver2;
        private final Panel panel;
        
        public SwerveXBox(Xbox driver1, Xbox driver2, Panel panel)
        {
            this.driver1 = driver1;
            this.driver2 = driver2;
            this.panel = panel;
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
         * Get the {@link Vector} (mag & angle) of the velocity joystick for Halo
         * Drive
         * 
         * @return The vector of the joystick.
         */
        public Vector getHaloDrive_Velocity()
        {
            Vector value = driver1.getVector(Hand.kLeft);
        
            if (value.getMag() < DEADBAND)
            {
                value.setMag(0);
            }
        
            return value;
        }
        
        /**
         * Get the heading/angle in degrees for Angle Drive
         * 
         * @return The angle in degrees of the joystick.
         */
        public double getAngleDrive_Heading()
        {
            double Angle = -1;
            
            if (driver1.getMagnitude(Hand.kRight) > 0.65)
            {
                Angle = driver1.getDirectionDegrees(Hand.kRight);
            }
        
            return Angle;
        }

        /**
         * Get the rotation for Angle Drive
         * 
         * @return The rotation rate in rad/s.
         */
        public double getAngleDrive_Rotation()
        {
            double Rotation = 0;
            int dpad = getDpad();
            
            if (dpad == 90)
            {
                Rotation = .75;
            }
            else if (dpad == 270)
            {
                Rotation = -.75;
            }
            return Rotation;
        }

        /**
         * Get the {@link Vector} (mag & angle) of the velocity joystick for Angle
         * Drive
         * 
         * @return The vector of the joystick.
         */
        public Vector getAngleDrive_Velocity()
        {
            Vector value = driver1.getVector(Hand.kLeft);
        
            if (value.getMag() < DEADBAND)
            {
                value.setMag(0);
            }
            return value;
        }

        public double getCrawlSpeed()
        {
            return driver1.getTriggerAxis(Hand.kLeft);
        }
        
        public int getDpad()
        {
            return driver1.getPOV(0);
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
