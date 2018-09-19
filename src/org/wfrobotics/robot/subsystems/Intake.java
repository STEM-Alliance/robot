package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.reuse.subsystems.background.BackgroundUpdate;
import org.wfrobotics.reuse.utilities.CircularBuffer;
import org.wfrobotics.reuse.utilities.ConsoleLogger;
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
public class Intake extends EnhancedSubsystem implements BackgroundUpdate
{
    static class SingletonHolder
    {
        static Intake instance = new Intake();
    }

    public static Intake getInstance()
    {
        return SingletonHolder.instance;
    }

    private final double kDistanceMaxIn;
    private final double kDistanceSensorPluggedIn = 3000.0;  // TODO Tune me
    private final double kJawsTimeout;

    private final RobotState state = RobotState.getInstance();
    private final TalonSRX master, follower;
    private final DoubleSolenoid cylinders;
    private final SharpDistance distanceSensor;
    private final CircularBuffer buffer;

    private boolean lastJawsState;
    private double lastJawsChangedTime;
    private double latestDistance;

    private Intake()
    {
        final RobotConfig config = RobotConfig.getInstance();
        final int bufferSize = 3;
        final double doesntHaveCubeDistance = 9999.0;  // Otherwise buffer initially filled with valid value

        kDistanceMaxIn = config.kIntakeDistanceToCube;
        kJawsTimeout = config.kJawsTimeoutSeconds;

        master = TalonFactory.makeTalon(config.kIntakeAddressR);
        master.setNeutralMode(NeutralMode.Brake);
        master.setInverted(config.kIntakeInvertR);
        master.configOpenloopRamp(.25, 10);
        TalonFactory.configCurrentLimiting(master, 10, 30, 100);
        TalonFactory.configOpenLoopOnly(master);

        follower = TalonFactory.makeFollowerTalon(config.kIntakeAddressL, master);
        follower.setNeutralMode(NeutralMode.Brake);
        follower.setInverted(config.kIntakeInvertL);

        cylinders = new DoubleSolenoid(config.kPCMAddress, config.kIntakeSolenoidF, config.kIntakeSolenoidR);

        distanceSensor = new SharpDistance(config.kIntakeInfrared);
        buffer = new CircularBuffer(bufferSize, doesntHaveCubeDistance);

        // Force defined states
        lastJawsChangedTime = Timer.getFPGATimestamp() - config.kJawsTimeoutSeconds * 1.01;
        lastJawsState = true;
        latestDistance = doesntHaveCubeDistance;
        setJaws(!lastJawsState);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new SmartIntake());
    }

    public void updateSensors(boolean isDisabled)
    {
        latestDistance = buffer.getAverage();
        state.updateIntake(latestDistance);

        if (isDisabled)
        {
            onBackgroundUpdate();  // Pump cube measurement for debugging sensor
            testSensorPluggedIn(false);
        }
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
        buffer.addFirst(rawDistance());
    }

    public boolean getJawsState()
    {
        return lastJawsState;
    }

    public boolean isSensorPluggedIn()
    {
        return latestDistance < kDistanceSensorPluggedIn;
    }

    public void setMotors(double percentageOut)
    {
        master.set(ControlMode.PercentOutput, percentageOut);
    }

    public boolean setJaws(boolean extendedOpen)
    {
        final double now = Timer.getFPGATimestamp();
        final boolean delayedEnough = now - lastJawsChangedTime > kJawsTimeout;
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

    private double rawDistance()
    {
        return distanceSensor.getDistanceInches() - kDistanceMaxIn;
    }

    private boolean testSensorPluggedIn(boolean printOnSuccess)
    {
        boolean sensorOkay = isSensorPluggedIn();
        String msg = String.format("Infared distance sensor %s", (sensorOkay) ? "okay" : "not plugged in");
        if (printOnSuccess)
        {
            System.out.println(msg);
        }
        else if (!sensorOkay)
        {
            ConsoleLogger.error(msg);
        }
        return sensorOkay;
    }

    public boolean runFunctionalTest(boolean includeMotion)
    {
        boolean result = true;

        result &= getDefaultCommand().doesRequire(this);
        result &= TalonChecker.checkFirmware(master);
        result &= TalonChecker.checkFirmware(follower);
        result &= TalonChecker.checkFrameRates(master);
        result &= testSensorPluggedIn(true);

        if (includeMotion)
        {
            setMotors(0.3);
            Timer.delay(0.75);
            setMotors(0.0);
        }

        return result;
    }
}

