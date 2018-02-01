package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.DistanceIntake;
import org.wfrobotics.robot.config.RobotMap;

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
    public SharpDistance uSensor;

    protected final RobotState state = RobotState.getInstance();

    public IntakeSubsystem()
    {
        leftIntake = new TalonSRX(RobotMap.CAN_INTAKE_LEFT);
        rightIntake = new TalonSRX(RobotMap.CAN_INTAKE_RIGHT);

        rightIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.set(ControlMode.Follower, RobotMap.CAN_INTAKE_RIGHT);

        rightIntake.setInverted(true);
        leftIntake.setInverted(true);

        uSensor = new SharpDistance(RobotMap.DIGITAL_INFARAD_SENSOR);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new DistanceIntake());
    }
    public void setMotor(double speed)
    {
        rightIntake.set(ControlMode.Current, speed);
    }
    public void stopMotor()
    {
        rightIntake.set(ControlMode.Current, 0);
    }
    public boolean hasCube()
    {
        return uSensor.getDistance() < 10;
    }
    public void pushToRobotState()
    {
        state.updateIntakeSensor(getDistance());
    }

    public double getDistance()
    {
        return uSensor.getDistance();
    }
}

