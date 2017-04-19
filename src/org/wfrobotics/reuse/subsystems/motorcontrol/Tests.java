package org.wfrobotics.reuse.subsystems.motorcontrol;

import org.wfrobotics.reuse.subsystems.motorcontrol.PositionMotorPot.PositionMotorPotConfig;

public class Tests 
{
    public static void main(String[] args)
    {
        // In config package
        double[] topBotTransport = {0, 10, 3};
        PositionConfig c = new PositionMotorPotConfig(0);
        c.positions = topBotTransport;
        c.tolerance = .5;
        
        // In Robot.java
        PositionSubsystem p = new PositionSubsystem(c);
    }
}
