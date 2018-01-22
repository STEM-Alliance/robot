package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.DriveStop;
import org.wfrobotics.robot.RobotState;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Motion magic test hardware */
public class TankSubsystem extends Subsystem
{
    final int kHalfRobotUpdateRate = 10;
    final int kPIDConstantsIndex = 0;
    final int kPIDSlot = 0;
    final int kTicksPerRev = 4096;
    final int kTimeoutMs = 10;

    private final RobotState state = RobotState.getInstance();

    TalonSRX motor;

    public TankSubsystem()
    {
        motor = new TalonSRX(10);

        // Setup sensor
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, kPIDConstantsIndex, kTimeoutMs);
        motor.setSensorPhase(false);  // Flip if motor direction correct but error increases without bound
        motor.setInverted(false);  // FLip if sensor error working but normal motor direction incorrect

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

        // TODO Dynamic talons, need switching setting in method
        configDistanceControl();

        // TODO Transfer to TalonFactory once understood
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new DriveStop());
    }

    public void doMagic(double output)
    {
        double targetRotations = 2;
        double targetPosition = output * kTicksPerRev * targetRotations;

        SmartDashboard.putNumber("Target Position", targetPosition);

        motor.setSelectedSensorPosition(0, kPIDConstantsIndex, kTimeoutMs);
        motor.set(ControlMode.MotionMagic, targetPosition);
    }

    public void doDrive(double output)
    {
        motor.set(ControlMode.PercentOutput, output);
    }

    public void update()
    {
        SmartDashboard.putNumber("Position", motor.getSelectedSensorPosition(kPIDConstantsIndex));
        SmartDashboard.putNumber("Velocity", motor.getSelectedSensorVelocity(kPIDConstantsIndex));
        SmartDashboard.putNumber("Trajectory Position", motor.getActiveTrajectoryPosition());
        SmartDashboard.putNumber("Trajectory Velocity", motor.getActiveTrajectoryVelocity());
        SmartDashboard.putNumber("Percent Output", motor.getMotorOutputPercent());
        SmartDashboard.putNumber("Error", motor.getClosedLoopError(kPIDConstantsIndex));

        SmartDashboard.putNumber("Driven", state.robotDistanceDriven);

        if (motor.getControlMode() == ControlMode.MotionMagic)
        {
            // TODO Needs to end if path is done
            // TODO report in inches
            state.updateRobotDistanceDriven(motor.getSelectedSensorPosition(kPIDConstantsIndex));  // TODO
        }
    }

    public double getError()
    {
        // TODO See triangle/trapizoid graph on shuffleboard
        // TODO See at no load and loaded motor

        return motor.getSelectedSensorVelocity(0);
    }

    private void configDistanceControl()
    {

    }
}