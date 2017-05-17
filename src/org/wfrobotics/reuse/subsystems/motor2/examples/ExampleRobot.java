package org.wfrobotics.reuse.subsystems.motor2.examples;

import org.wfrobotics.reuse.subsystems.motor2.HerdMotorSubsystem;
import org.wfrobotics.reuse.subsystems.motor2.examples.arm.ArmConfig;
import org.wfrobotics.reuse.subsystems.motor2.examples.shooter.ShooterConfig;

/**
 * Think of this as Robot.java, which will create one instance of each Subsystem of the Robot
 * @author Team 4818 WFRobotics
 */
public class ExampleRobot
{
    public static HerdMotorSubsystem arm;
    public static HerdMotorSubsystem shooter;
    
    public static void main(String[] args)
    {
        arm = new HerdMotorSubsystem("Arm", ArmConfig.getMotor(), ArmConfig.getDefaultCommand());
        shooter = new HerdMotorSubsystem("Shooter", ShooterConfig.getMotor(), ShooterConfig.getDefaultCommand());
    }
}