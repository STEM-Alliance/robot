package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.Elevate;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LiftSubsystem extends Subsystem {
    public TalonSRX LiftMotor;

    public LiftSubsystem()
    {
        this.LiftMotor = new TalonSRX(18);
    }
    public static boolean isAtTop()
    {
        return false;

    }
    public static boolean isAtBottom()
    {
        return false;
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {

        // Set the default command for a subsystem here.
        setDefaultCommand(new Elevate(-.1));
    }
}

