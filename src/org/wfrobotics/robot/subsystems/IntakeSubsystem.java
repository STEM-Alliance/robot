package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.commands.IntakePull;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class IntakeSubsystem extends Subsystem
{
    // TODO Make these private, which is going to mean we need at least one setter method
    public TalonSRX leftIntake;
    public TalonSRX rightIntake;

    public IntakeSubsystem()
    {
        // TODO Use Talon factory makeTalon() at the very least.
        leftIntake = new TalonSRX(19);  // TODO Add configuration to Config/RobotMap
        rightIntake = new TalonSRX(20);  // TODO Add configuration to Config/RobotMap
        rightIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.set(ControlMode.Follower, 20);  // TODO Use variable so this never becomes broken if master talon address changes
        rightIntake.setInverted(false);
        leftIntake.setInverted(true);

        // TODO Turn down frame rates if intake doesn't utilize closed loop control mode - Allows more CAN message bandwidth for lift, drives, etc

        // TODO Other settings helpful? Skim through the Phoenix user manual or talon object's methods
    }

    // TODO When the lift is up, the intake cannot let go of the cube. Default command needs to be smarter and reference robot states
    public void initDefaultCommand()
    {
        // Set the default command for a subsystem here.
        setDefaultCommand(new IntakePull(0));
    }

    // TODO Setter method for scoring power cube in vault. Optimize for exhausting in that use case.
    //      Perhaps require lift to be in a state ideal for that?

    // TODO Setter method for scoring power cube on scale. Optimize for exhausting in that use case.
    //      Perhaps do something special in tandum with pneumatics? Or is this just pneumatics?

    // TODO Setter method for scoring power cube on switch. Optimize for exhausting in that use case.
    //      Probably want to push the cube out faster than when exhausting on scale. Need difference commands?
    //      Beast mode - Intake or Intake commands know how fast to exhaust (partially or entirely) based on lift height

    // TODO Do we want to do anything special while the robot is moving? What should the wheels do the "transport" mode? Should the wheels disallow release?

    // TODO Should the intake automatically sense the cube, and if it's trying to slip out, automatically try to recover? Pulse the wheels?
}
