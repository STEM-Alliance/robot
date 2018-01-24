package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.DriveStop;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.subsystems.drive.DifferentialDrive;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Motion magic test hardware */
public class MagicTankSubsystem extends Subsystem implements DifferentialDrive
{
    int kPIDConstantsIndex = 0;
    final int kHalfRobotUpdateRate = 10;
    final int kPIDSlot = 0;
    final int kTicksPerRev = 4096;
    final int kTimeoutMs = 10;
    final double kWheelDiameter = 1 / Math.PI;

    private static MagicTankSubsystem instance = null;
    private final RobotState state = RobotState.getInstance();
    protected final TalonSRX motor;

    public MagicTankSubsystem()
    {
        // TODO See documentation for how to actually determine values - these are from the example
        motor = TalonSRXFactory.makeConstAccelControlTalon(10, .25, .005, .75, .25, 0, 15000, 12000);

        // TODO Try SetVelocityMeasurementPeriod and SetVelocityMeasurementWindow new equivalent, try 5ms status frame rate, try voltage ramp rate

        motor.setSensorPhase(false);
        motor.setInverted(false);
        motor.config_IntegralZone(0, 5, kTimeoutMs);  // Allows I to drift to exact spot without motor chatter
        motor.setNeutralMode(NeutralMode.Brake);      // Improves repeatability to exact spot without adding an offset

        // TODO Feels like minimally tuned system is great for distance driving, just good for position control - would need zero
    }

    public static MagicTankSubsystem getInstance()
    {
        if (instance == null) { instance = new MagicTankSubsystem(); }
        return instance;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new DriveStop());
    }

    public void driveBasic(HerdVector vector)
    {
        motor.set(ControlMode.PercentOutput, vector.getMag());
        update();
    }

    public void turnBasic(HerdVector vector)
    {
    }

    public void driveDistance(double inchesForward)
    {
        double targetPositionTicks = inchesForward / kWheelDiameter / Math.PI * kTicksPerRev;

        SmartDashboard.putNumber("Target Position", targetPositionTicks);

        motor.set(ControlMode.MotionMagic, targetPositionTicks);
        update();
    }

    public void resetDistanceDriven()
    {
        motor.setSelectedSensorPosition(0, 0, kTimeoutMs);
        state.resetRobotDistanceDriven();
    }

    public void setBrake(boolean enable)
    {
    }

    public void setGear(boolean useHighGear)
    {
    }

    public void zeroGyro()
    {
    }

    public void driveDifferential(double left, double right)
    {
    }

    private void update()
    {
        SmartDashboard.putNumber("Position", motor.getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Velocity", motor.getSelectedSensorVelocity(0));
        SmartDashboard.putNumber("Trajectory Position", motor.getActiveTrajectoryPosition());
        SmartDashboard.putNumber("Trajectory Velocity", motor.getActiveTrajectoryVelocity());
        SmartDashboard.putNumber("Percent Output", motor.getMotorOutputPercent());
        SmartDashboard.putNumber("Error", motor.getClosedLoopError(0));

        double inchesForward = motor.getSelectedSensorPosition(0) / kTicksPerRev * kWheelDiameter * Math.PI;
        state.updateRobotDistanceDriven(inchesForward);

        SmartDashboard.putNumber("Driven", state.robotDistanceDriven);
    }
}