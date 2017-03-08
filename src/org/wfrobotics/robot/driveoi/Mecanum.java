package org.wfrobotics.robot.driveoi;

import org.wfrobotics.controller.Xbox;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Mecanum 
{
    public interface MecanumOI
    {
        public double getX();
        public double getY();
        public double getRotation();
    }

    public static class MecanumXBox implements MecanumOI
    {
        private final Xbox controller;
        
        public MecanumXBox(Xbox controller)
        {
            this.controller = controller;
        }

        public double getX()
        {
            return controller.getX(Hand.kLeft);
        }
        
        public double getY()
        {
            return controller.getY(Hand.kLeft);
        }
        
        public double getRotation()
        {
            return controller.getX(Hand.kRight);
        }
    }
}
