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
    private final double kDistanceMaxIn;
    private final double kTimeoutHorizontal;

    private static IntakeSubsystem instance = null;
    private final RobotState state = RobotState.getInstance();
    private final TalonSRX master;
    private final TalonSRX follower;
    private final DoubleSolenoid cylinders;
    private final SharpDistance distanceSensor;
    private final CircularBuffer buffer;

    private boolean lastJawsState;
    private double lastJawsChangedTime;
    private double latestDistance;

    private IntakeSubsystem()
    {
        final RobotConfig config = RobotConfig.getInstance();
        final int bufferSize = 3;

        kDistanceMaxIn = config.INTAKE_DISTANCE_TO_CUBE;
        kTimeoutHorizontal = config.INTAKE_TIMEOUT_WRIST;

        master = TalonFactory.makeTalon(config.CAN_INTAKE_RIGHT);
        TalonFactory.configOpenLoopOnly(master);
        master.setNeutralMode(NeutralMode.Brake);
        master.setInverted(config.INTAKE_INVERT_RIGHT);
        master.configOpenloopRamp(.25, 10);
        TalonFactory.configCurrentLimiting(master, 30, 100, 10);

        follower = TalonFactory.makeFollowerTalon(config.CAN_INTAKE_LEFT, master);
        follower.setNeutralMode(NeutralMode.Brake);
        follower.setInverted(config.INTAKE_INVERT_LEFT);

        cylinders = new DoubleSolenoid(config.CAN_PNEUMATIC_CONTROL_MODULE, config.PNEUMATIC_INTAKE_HORIZONTAL_FORWARD, config.PNEUMATIC_INTAKE_HORIZONTAL_REVERSE);

        distanceSensor = new SharpDistance(config.INTAKE_SENSOR_R);
        buffer = new CircularBuffer(bufferSize, 9999);  // Filled with zeros by default, which is bad because it's a valid value

        // Force defined states
        lastJawsChangedTime = Timer.getFPGATimestamp() - config.INTAKE_TIMEOUT_WRIST * 1.01;
        lastJawsState = true;
        latestDistance = 9999.0;
        setJaws(!lastJawsState);
    }

    public static IntakeSubsystem getInstance()
    {
        if (instance == null)
        {
            instance = new IntakeSubsystem();
        }
        return instance;
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
        buffer.addFirst(distanceSensor.getDistanceInches() * 2.54 - kDistanceMaxIn);
    }

    public boolean getJawsState()
    {
        return lastJawsState;
    }

    public void setIntake(double percentageOutward)
    {
        master.set(ControlMode.PercentOutput, percentageOutward);
    }

    public boolean setJaws(boolean extendedOpen)
    {
        final boolean delayedEnough = Timer.getFPGATimestamp() - lastJawsChangedTime > kTimeoutHorizontal;
        final boolean different = extendedOpen != lastJawsState;
        boolean stateChanged = false;

        if (delayedEnough && different)
        {
            cylinders.set(extendedOpen ? Value.kForward : Value.kReverse);
            lastJawsChangedTime = Timer.getFPGATimestamp();
            lastJawsState = extendedOpen;
            stateChanged = true;
        }
        return stateChanged;
    }

    public boolean runFunctionalTest()
    {
        boolean result = true;

        result &= TalonFactory.checkFirmware(master);
        result &= TalonFactory.checkFirmware(follower);
        result &= TalonFactory.checkEncoder(master);

        // TODO Check infared < some value == plugged in?

        System.out.println(String.format("Lift Test: %s", (result) ? "SUCCESS" : "FAILURE"));
        return result;
    }
}

