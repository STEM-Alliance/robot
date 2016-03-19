package com.taurus.subsystems;

import com.taurus.commands.ClimberClaw;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ClimberClawSubsystem extends Subsystem {
    

    private static final double RELEASE_ANGLE = 180;
    private static final double HOLD_ANGLE = 0;
    private Servo clawRelease;
    
    public ClimberClawSubsystem()
    {
        clawRelease = new Servo(RobotMap.PIN_SERVO_CLAW_RELEASE);
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ClimberClaw(false));
    }

    public void release()
    {
        clawRelease.setAngle(RELEASE_ANGLE);
    }
    public void hold()
    {
        clawRelease.setAngle(HOLD_ANGLE);
    }
}

