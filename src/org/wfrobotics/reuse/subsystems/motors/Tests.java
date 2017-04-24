package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.subsystems.motors.PositionMotor;
import org.wfrobotics.reuse.subsystems.motors.PositionMotorPot;

public class Tests
{
    public static void main(String[] args)
    {
        // In config package
        double[] topBotTransport = {0, 10, 3};
        PositionMotor.Builder<?, ?> lifterMotorBuilder = new PositionMotorPot.Builder(2)
                                                             .positions(topBotTransport)
                                                             .tolerance(.5);
        
        System.out.println("Builder: " + lifterMotorBuilder);
        System.out.println("T motor: " + lifterMotorBuilder.motor);
        System.out.println("B thisBuilder: " + lifterMotorBuilder.thisBuilder.getClass());
        System.out.println("----------------------------------");
        
        // In Robot.java
        PositionSubsystem lifter = new PositionSubsystem(lifterMotorBuilder);
    }
}