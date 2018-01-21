package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.DriveStop;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/** Motion magic test hardware */
public class TankSubsystem extends Subsystem
{
    int kHalfRobotUpdateRate = 10;
    int kPIDConstantsIndex = 0;
    int kPIDSlot = 0;
    int kTimeoutMs = 10;
    TalonSRX motor;

    public TankSubsystem()
    {
        motor = new TalonSRX(10);

        // Setup sensor
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, kPIDConstantsIndex, kTimeoutMs);
        motor.setSensorPhase(true);
        motor.setInverted(false);

        // Frame rate must be > robot update rate
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, kHalfRobotUpdateRate, kTimeoutMs);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, kHalfRobotUpdateRate, kTimeoutMs);

        // Output limits
        motor.configNominalOutputForward(0, kTimeoutMs);
        motor.configNominalOutputReverse(0, kTimeoutMs);
        motor.configPeakOutputForward(1, kTimeoutMs);
        motor.configPeakOutputReverse(-1, kTimeoutMs);

        // TODO See documentation for how to actually determine values - these are from the example
        // Closed loop gains
        motor.selectProfileSlot(kPIDSlot, kPIDConstantsIndex);
        motor.config_kF(0, 0.2, kTimeoutMs);
        motor.config_kP(0, 0.2, kTimeoutMs);
        motor.config_kI(0, 0, kTimeoutMs);
        motor.config_kD(0, 0, kTimeoutMs);

        // TODO See documentation for how to actually determine values - these are from the example
        // Accelerator & velocity constraints
        motor.configMotionCruiseVelocity(15000, kTimeoutMs);
        motor.configMotionAcceleration(6000, kTimeoutMs);

        // Zero sensor
        motor.setSelectedSensorPosition(0, kPIDConstantsIndex, kTimeoutMs);

        // TODO Transfer to TalonFactory once understood
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new DriveStop());
    }

    public void doMagic(double output)
    {
        motor.set(ControlMode.MotionMagic, output);
        //motor.set(ControlMode.PercentOutput, output);

        // TODO Update robot state distance driven
    }

    public double getError()
    {
        // TODO See triangle/trapizoid graph on shuffleboard
        // TODO See at no load and loaded motor

        return motor.getSelectedSensorVelocity(0);
    }

    public ControlMode getMode()
    {
        // TODO Delete once verified?

        return motor.getControlMode();
    }
}