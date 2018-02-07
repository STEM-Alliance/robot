package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.intake.DistanceIntake;
import org.wfrobotics.robot.config.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class IntakeSubsystem extends Subsystem
{
    /*
     * when the block is further away from the sensor the motors are at speed x
     * when it comes closer to the sensor we want to ramp down the motors to speed 2/3x
     * when distances is equal to 0, the motor speed is set to 0
     */
    public static final double kDistanceHasCube = 20; // (in centmeters)

    protected final RobotState state = RobotState.getInstance();
    private TalonSRX leftIntake;
    private TalonSRX rightIntake;
    private SharpDistance uSensor;

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

    public void setMotor(double percentage)
    {
        rightIntake.set(ControlMode.PercentOutput, percentage);
    }

    public void update()
    {
        double centimeters = uSensor.getDistance();

        SmartDashboard.putNumber("Distance:", centimeters);
        state.updateIntakeSensor(centimeters);
        state.updateHasCube(uSensor.getDistance() < kDistanceHasCube);
    }
}

