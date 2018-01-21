package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.commands.IntakePull;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class IntakeSubsystem extends Subsystem
{
    public TalonSRX leftIntake;
    public TalonSRX rightIntake;

    public IntakeSubsystem()
    {
        leftIntake = new TalonSRX(19);
        rightIntake = new TalonSRX(20);
        rightIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.set(ControlMode.Follower, 20);
        rightIntake.setInverted(true);
        leftIntake.setInverted(true);
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void initDefaultCommand()
    {
        // Set the default command for a subsystem here.
        setDefaultCommand(new IntakePull(0));
    }
}
