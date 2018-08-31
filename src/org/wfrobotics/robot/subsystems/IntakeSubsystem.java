package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.reuse.subsystems.background.BackgroundUpdate;
import org.wfrobotics.reuse.utilities.CircularBuffer;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The intake consists of two 775pro motors to pull in the cube,
 * two pneumatic cylinders for compliance and controlled release,
 * and an infared distance sensor for our automated SmartIntake
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class IntakeSubsystem extends EnhancedSubsystem implements BackgroundUpdate
{
    private final double kDistanceMaxIn;
    private final double kDistanceSensorPluggedIn = 3000.0;  // TODO Tune me
    private final double kTimeoutHorizontal;

    private static IntakeSubsystem instance = null;
    private final RobotState state = RobotState.getInstance();
    private final TalonSRX master, follower;
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
        final double doesntHaveCubeDistance = 9999.0;  // Otherwise buffer initially filled with valid value

        kDistanceMaxIn = config.INTAKE_DISTANCE_TO_CUBE;
        kTimeoutHorizontal = config.INTAKE_TIMEOUT_WRIST;

        master = TalonFactory.makeTalon(config.CAN_INTAKE_RIGHT);
        TalonFactory.configOpenLoopOnly(master);
        master.setNeutralMode(NeutralMode.Brake);
        master.setInverted(config.INTAKE_INVERT_RIGHT);
        master.configOpenloopRamp(.25, 10);
        TalonFactory.configCurrentLimiting(master, 10, 30, 100);

        follower = TalonFactory.makeFollowerTalon(config.CAN_INTAKE_LEFT, master);
        follower.setNeutralMode(NeutralMode.Brake);
        follower.setInverted(config.INTAKE_INVERT_LEFT);

        cylinders = new DoubleSolenoid(config.CAN_PNEUMATIC_CONTROL_MODULE, config.PNEUMATIC_INTAKE_HORIZONTAL_FORWARD, config.PNEUMATIC_INTAKE_HORIZONTAL_REVERSE);

        distanceSensor = new SharpDistance(config.INTAKE_SENSOR_R);
        buffer = new CircularBuffer(bufferSize, doesntHaveCubeDistance);

        // Force defined states
        lastJawsChangedTime = Timer.getFPGATimestamp() - config.INTAKE_TIMEOUT_WRIST * 1.01;
        lastJawsState = true;
        latestDistance = doesntHaveCubeDistance;
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
        state.updateIntake(latestDistance);
    }

    public void reportState()
    {
        SmartDashboard.putNumber("Cube", latestDistance);
    }

    public void onStartUpdates(boolean isAutonomous)
    {

    }

    public void onBackgroundUpdate()
    {
        buffer.addFirst(getRawDistance());
    }

    public boolean getJawsState()
    {
        return lastJawsState;
    }

    public boolean isSensorPluggedIn()
    {
        return getRawDistance() < kDistanceSensorPluggedIn;
    }

    public void setIntake(double percentageOutward)
    {
        master.set(ControlMode.PercentOutput, percentageOutward);
    }

    public boolean setJaws(boolean extendedOpen)
    {
        final double now = Timer.getFPGATimestamp();
        final boolean delayedEnough = now - lastJawsChangedTime > kTimeoutHorizontal;
        final boolean different = extendedOpen != lastJawsState;
        boolean stateChanged = false;

        if (delayedEnough && different)
        {
            cylinders.set(extendedOpen ? Value.kForward : Value.kReverse);
            lastJawsChangedTime = now;
            lastJawsState = extendedOpen;
            stateChanged = true;
        }
        return stateChanged;
    }

    private double getRawDistance()
    {
        return distanceSensor.getDistanceInches() * 2.54 - kDistanceMaxIn;
    }

    public boolean runFunctionalTest(boolean includeMotion)
    {
        boolean result = true;
        boolean sensorOkay;

        System.out.println("Intake Test:");
        result &= TalonChecker.checkFirmware(master);
        result &= TalonChecker.checkFirmware(follower);
        result &= TalonChecker.checkFrameRates(master);

        sensorOkay = isSensorPluggedIn();
        System.out.println(String.format("Infared distance sensor %s", (sensorOkay) ? "okay" : "not plugged in"));
        result &= sensorOkay;

        setIntake(0.3);
        Timer.delay(0.75);
        setIntake(0.0);

        System.out.println(String.format("Intake Test: %s", (result) ? "SUCCESS" : "FAILURE"));
        return result;
    }
}

