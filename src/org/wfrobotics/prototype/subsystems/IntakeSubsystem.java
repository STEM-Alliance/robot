package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.IntakePull;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
    public class IntakeSubsystem extends Subsystem {
        public CANTalon leftIntake;
        public CANTalon rightIntake;

    public IntakeSubsystem()
    {
        this.leftIntake = new  CANTalon(19);
        this.rightIntake = new CANTalon(20);

        leftIntake.changeControlMode(TalonControlMode.Follower);
        leftIntake.set(20);
        rightIntake.setInverted(true);

    }
        // Put methods for controlling this subsystem
        // here. Call these from Commands.

        public void initDefaultCommand() {
            // Set the default command for a subsystem here.
            setDefaultCommand(new IntakePull(0));
        }
    }

