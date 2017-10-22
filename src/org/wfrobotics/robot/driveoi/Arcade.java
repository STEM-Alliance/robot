package org.wfrobotics.robot.driveoi;

import org.wfrobotics.reuse.controller.Xbox;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Arcade
{
    public interface ArcadeIO
    {
        public double getThrottle();
        public double getTurn();
        public double getThrottleSpeedAdjust();
    }

    public static class ArcadeXbox implements ArcadeIO
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
            return 0.5 + .5 * controller.getTrigger(Hand.kLeft);
        }
    }
}
