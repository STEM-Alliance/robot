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
    final double kWheelCircumference = 1; //* Math.PI;

    private static MagicTankSubsystem instance = null;
    private final RobotState state = RobotState.getInstance();
    protected final TalonSRX motor;

    private double positionStart = 0;
    private double lastWanted = 0;
    public MagicTankSubsystem()
    {
        // TODO See documentation for how to actually determine values - these are from the example
        motor = TalonSRXFactory.makeConstAccelControlTalon(10, .25, .005, .75, .25, 0, 12000, 9000);

        motor.setSensorPhase(false);
        motor.setInverted(false);
        motor.config_IntegralZone(0, 5, kTimeoutMs);  // Allows I to drift to exact spot without motor chatter
        motor.setNeutralMode(NeutralMode.Brake);      // Improves repeatability to exact spot without adding an offset

        // TODO Feels like minimally tuned system is great for distance driving, just good for position control - would need zero

        // Page 39 - Frame rates: Quad encoder, applied output, applied control mode, closed loop error, sensor position (appears redundant with default encoders?)
        // Page 40 - Print out sensor position vs quadrature position
        // Page 41 - Ramp rate configs apply regardless of mode (including follower). Should we set on slaves?
        // Page 41 - Closed loop ramp rate introduce oscillations typically if not zero or very small
        // Page 49 - Need to read & apply Using multiple talon from one sensor tips

        positionStart = motor.getSensorCollection().getQuadraturePosition();
        lastWanted = positionStart;
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
        reportDistanceDriven();
    }

    public void turnBasic(HerdVector vector)
    {
    }

    public void driveDistanceInit(double inchesForward)
    {
        positionStart = lastWanted;  // Last command without error
        double relativePositionTicks = inchesForward * (kWheelCircumference * kTicksPerRev);
        double absolutePositionTicks = relativePositionTicks + positionStart;

        lastWanted = absolutePositionTicks;
        SmartDashboard.putNumber("Target Position", absolutePositionTicks);

        motor.set(ControlMode.MotionMagic, absolutePositionTicks);

        state.resetRobotDistanceDriven();  // TODO we don't need this method anymore
    }

    public void driveDistanceUpdate()
    {
        //motor.setSelectedSensorPosition(0, 0, 0);
        reportDistanceDriven();
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

    private void reportDistanceDriven()
    {
        double ticksAbsolute = motor.getSensorCollection().getQuadraturePosition();
        double inchesActual = (ticksAbsolute - positionStart) / (kWheelCircumference * kTicksPerRev);

        SmartDashboard.putNumber("Position", ticksAbsolute);
        SmartDashboard.putNumber("Velocity", motor.getSelectedSensorVelocity(0));
        SmartDashboard.putNumber("Trajectory Position", motor.getActiveTrajectoryPosition());
        SmartDashboard.putNumber("Trajectory Velocity", motor.getActiveTrajectoryVelocity());
        SmartDashboard.putNumber("Percent Output", motor.getMotorOutputPercent());
        SmartDashboard.putNumber("Error", motor.getClosedLoopError(0));

        state.updateRobotDistanceDriven(inchesActual);

        SmartDashboard.putNumber("Driven", state.robotDistanceDriven);
    }
}