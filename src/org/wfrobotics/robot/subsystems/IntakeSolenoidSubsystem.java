package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.commands.IntakeSolenoid;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class IntakeSolenoidSubsystem extends Subsystem {
    public DoubleSolenoid intakeSolenoid;
    boolean isHigh;
    public IntakeSolenoidSubsystem()
    {
        this.intakeSolenoid = new DoubleSolenoid(7, 3, 4);

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void intakeSolenoidSet (boolean high)
    {
        intakeSolenoid.set(high ? Value.kForward : Value.kReverse);
        this.isHigh = high;

    }
    public void initDefaultCommand() {

        // Set the default command for a subsystem here.
        setDefaultCommand(new IntakeSolenoid(false));
    }
}

