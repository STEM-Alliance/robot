package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.commands.IntakeSolenoid;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

public class IntakeSolenoidSubsystem extends Subsystem
{
    public DoubleSolenoid intakeSolenoids;
    boolean isHigh;
    // TODO Do we care about knowing exactly when the state changes? Would require using time since request for state change

    public IntakeSolenoidSubsystem()
    {
        intakeSolenoids = new DoubleSolenoid(7, 3, 4);  // TODO Add configuration to Config/RobotMap
    }

    // TODO When the lift is up, the intake cannot let go of the cube. Default command needs to be smarter and reference robot state
    public void initDefaultCommand()
    {
        setDefaultCommand(new IntakeSolenoid(false));  // TODO Add configuration to Config/Commands?
    }

    public void intakeSolenoidSet (boolean high)
    {
        intakeSolenoids.set(high ? Value.kForward : Value.kReverse);
        isHigh = high;
    }

    // TODO Should we allow the driver to change the solenoid direction at any time? Any advantage to a minimum time they try to move to prevent chatter on button smashing?

    // TODO Probably want one or more command groups that use the wheels and pneumatics at the same time? Is that in all cases? Try to figure out what's good by experimenting touch the cube in different ways and different circumstances with the robot.
}

