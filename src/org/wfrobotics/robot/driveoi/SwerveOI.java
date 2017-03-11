package org.wfrobotics.robot.driveoi;

import org.wfrobotics.Vector;

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
