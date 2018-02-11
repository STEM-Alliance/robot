package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.intake.DistanceIntakeTriggers;
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
    private TalonSRX masterRight;
    private TalonSRX followerLeft;
    private SharpDistance uSensor;

    public IntakeSubsystem()
    {
        masterRight = TalonSRXFactory.makeTalon(RobotMap.CAN_INTAKE_RIGHT);
        followerLeft = TalonSRXFactory.makeFollowerTalon(RobotMap.CAN_INTAKE_LEFT, RobotMap.CAN_INTAKE_RIGHT);
        masterRight.setNeutralMode(NeutralMode.Brake);
        followerLeft.setNeutralMode(NeutralMode.Brake);
        masterRight.setInverted(false);
        followerLeft.setInverted(false);

        uSensor = new SharpDistance(RobotMap.ANALOG_INTAKE_DISTANCE);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new DistanceIntakeTriggers());
    }

    public void setMotor(double percentage)
    {
        masterRight.set(ControlMode.PercentOutput, percentage);
    }

    public void update()
    {
        double centimeters = uSensor.getDistance();

        SmartDashboard.putNumber("Distance:", centimeters);
        state.updateIntakeSensor(centimeters);
        state.updateHasCube(uSensor.getDistance() < kDistanceHasCube);
    }
}

