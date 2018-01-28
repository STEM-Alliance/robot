package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.robot.commands.DistanceIntake;

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

    public IntakeSubsystem()
    {
        leftIntake = new TalonSRX(10);
        rightIntake = new TalonSRX(10);
        rightIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.setNeutralMode(NeutralMode.Brake);
        leftIntake.set(ControlMode.Follower, 10);

        rightIntake.setInverted(true);
        leftIntake.setInverted(true);

        uSensor = new SharpDistance(0);
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
    public double getDistance()
    {
        return uSensor.getDistance();
    }
}
