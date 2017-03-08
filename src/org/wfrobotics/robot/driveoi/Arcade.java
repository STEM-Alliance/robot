package org.wfrobotics.robot.driveoi;

import org.wfrobotics.controller.Xbox;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Arcade 
{
    public interface ArcadeOI
    {
        public double getThrottle();
        public double getTurn();
        public double getThrottleSpeedAdjust();
    }
    
    public class ArcadeXbox implements ArcadeOI
    {
        private final Xbox controller;
        
        public ArcadeXbox(Xbox controller)
        {
            this.controller = controller;
        }
        
        public double getThrottle()
        {
            return controller.getY(Hand.kLeft);
        }
        
        public double getTurn()
        {
            return controller.getX(Hand.kLeft);
        }
        
        public double getThrottleSpeedAdjust()
        {
            return 0.5 + .5 * controller.getTriggerAxis(Hand.kLeft);
        }
    }
}
