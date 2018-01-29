package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.robot.commands.DistanceIntake;
import org.wfrobotics.robot.config.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class IntakeSubsystem extends Subsystem
{
    // TODO Make these private, which is going to mean we need at least one setter method
    public TalonSRX leftIntake;
    public TalonSRX rightIntake;
    public SharpDistance uSensor;

    public IntakeSubsystem()
    {
        int master = RobotMap.CAN_INTAKE_RIGHT;
        int slave = RobotMap.CAN_INTAKE_LEFT;

        leftIntake = TalonSRXFactory.makeTalon(slave);
        rightIntake = TalonSRXFactory.makeTalon(master);
        rightIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.set(ControlMode.Follower, master);
        rightIntake.setInverted(true);
        leftIntake.setInverted(true);

        uSensor = new SharpDistance(RobotMap.DIGITAL_INTAKE_DISTANCE);

        // TODO Turn down frame rates if intake doesn't utilize closed loop control mode - Allows more CAN message bandwidth for lift, drives, etc

        // TODO Other settings helpful? Skim through the Phoenix user manual or talon object's methods
    }

    // TODO When the lift is up, the intake cannot let go of the cube. Default command needs to be smarter and reference robot states
    public void initDefaultCommand()
    {
        setDefaultCommand(new DistanceIntake());
    }
    public void setMotor(double percentInward)
    {
        rightIntake.set(ControlMode.PercentOutput, percentInward);
    }

    public boolean hasCube()
    {
        return uSensor.getDistance() < 10;
    }

    public double getDistance()
    {
        return uSensor.getDistance();
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
