package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.reuse.subsystems.SAFMSubsystem;
import org.wfrobotics.reuse.subsystems.background.BackgroundUpdate;
import org.wfrobotics.reuse.utilities.CircularBuffer;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeSubsystem extends SAFMSubsystem implements BackgroundUpdate
{
    private final int bufferSize = 3;
    private final double kDistanceMaxIn;
    private final double kTimeoutHorizontal;
    private final RobotState state = RobotState.getInstance();

    private final TalonSRX masterRight;
    private final TalonSRX followerLeft;
    private final DoubleSolenoid horizontalIntake;
    private final SharpDistance distanceSensor;
    private final CircularBuffer buffer;

    private boolean lastHorizontalState;
    private double lastHorizontalTime;
    private double latestDistance;

    public IntakeSubsystem()
    {
        final RobotConfig config = RobotConfig.getInstance();

        masterRight = TalonFactory.makeTalon(config.CAN_INTAKE_RIGHT);
        TalonFactory.configOpenLoopOnly(masterRight);
        masterRight.setNeutralMode(NeutralMode.Brake);
        masterRight.setInverted(config.INTAKE_INVERT_RIGHT);
        masterRight.configOpenloopRamp(.25, 10);
        TalonFactory.configCurrentLimiting(masterRight, 30, 100, 10);

        followerLeft = TalonFactory.makeFollowerTalon(config.CAN_INTAKE_LEFT, config.CAN_INTAKE_RIGHT);
        followerLeft.setNeutralMode(NeutralMode.Brake);
        followerLeft.setInverted(config.INTAKE_INVERT_LEFT);

        horizontalIntake = new DoubleSolenoid(config.CAN_PNEUMATIC_CONTROL_MODULE, config.PNEUMATIC_INTAKE_HORIZONTAL_FORWARD, config.PNEUMATIC_INTAKE_HORIZONTAL_REVERSE);

        distanceSensor = new SharpDistance(config.INTAKE_SENSOR_R);
        buffer = new CircularBuffer(bufferSize, 9999);  // Filled with zeros by default, which is bad because it's a valid value

        kDistanceMaxIn = config.INTAKE_DISTANCE_TO_CUBE;
        kTimeoutHorizontal = config.INTAKE_TIMEOUT_WRIST;

        // Force defined states
        lastHorizontalTime = Timer.getFPGATimestamp() - config.INTAKE_TIMEOUT_WRIST * 1.01;
        lastHorizontalState = true;
        latestDistance = 9999;
        setHorizontal(!lastHorizontalState);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new SmartIntake());
    }

    public void updateSensors()
    {
        latestDistance = buffer.getAverage();
        state.updateIntakeSensor(latestDistance);
    }

    public void reportState()
    {
        SmartDashboard.putNumber("Cube", latestDistance);
    }

    public void onStart(boolean isAutonomous)
    {

    }

    public void onBackgroundUpdate()
    {
        buffer.addFirst(distanceSensor.getDistance() - kDistanceMaxIn);
    }

    public boolean getHorizontal()
    {
        return lastHorizontalState;
    }

    public void setIntake(double percentageOutward)
    {
        masterRight.set(ControlMode.PercentOutput, percentageOutward);
    }

    public boolean setHorizontal(boolean extendedOpen)
    {
        final boolean delayedEnough = Timer.getFPGATimestamp() - lastHorizontalTime > kTimeoutHorizontal;
        final boolean different = extendedOpen != lastHorizontalState;
        boolean stateChanged = false;

        if (delayedEnough && different)
        {
            horizontalIntake.set(extendedOpen ? Value.kForward : Value.kReverse);
            lastHorizontalTime = Timer.getFPGATimestamp();
            lastHorizontalState = extendedOpen;
            stateChanged = true;
        }
        return stateChanged;
    }
}

