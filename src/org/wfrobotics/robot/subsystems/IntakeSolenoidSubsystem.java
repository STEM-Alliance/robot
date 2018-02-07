package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.commands.intake.HorizontalIntake;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

public class IntakeSolenoidSubsystem extends Subsystem
{
    final public DoubleSolenoid horizontalIntake;
    final public DoubleSolenoid vertIntake;

    // TODO Do we care about knowing exactly when the state changes? Would require using time since request for state change
    public IntakeSolenoidSubsystem()
    {
        horizontalIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_HORIZONTAL_REVERSE);
        vertIntake = new DoubleSolenoid(RobotMap.CAN_PNEUMATIC_CONTROL_MODULE, RobotMap.PNEUMATIC_INTAKE_VERTICAL_FORWARD, RobotMap.PNEUMATIC_INTAKE_VERTICAL_REVERSE);
    }

    // TODO When the lift is up, the intake cannot let go of the cube. Default command needs to be smarter and reference robot state
    public void initDefaultCommand()
    {
        setDefaultCommand(new HorizontalIntake(false));
    }

    public void setHorizontal(boolean extended)
    {
        horizontalIntake.set(extended ? Value.kForward : Value.kReverse);
    }

    public void setVert(boolean high)
    {
        vertIntake.set(high ? Value.kForward : Value.kReverse);
    }

    // TODO Should we allow the driver to change the solenoid direction at any time? Any advantage to a minimum time they try to move to prevent chatter on button smashing?
}