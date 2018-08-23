package org.wfrobotics.robot.subsystems;

import java.util.List;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.SAFMSubsystem;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.lift.LiftAutoZeroThenPercentVoltage;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LiftSubsystem extends SAFMSubsystem
{
    private static final double kTicksPerRev = 4096.0;
    private static final double kRevsPerInch = 1.0 / 4.555;  // Measured on practice robot
    private static final double kFeedForwardHasCube = 0.0;   // TODO Tune me
    private static final double kFeedForwardNoCube = 0.0;    // TODO Tune me
    private final int kSlotUp;
    private final int kSlotDown;
    private final boolean kTuning;

    private static LiftSubsystem instance = null;
    private final RobotState state = RobotState.getInstance();
    private final TalonSRX[] motors = new TalonSRX[2];
    private final LimitSwitch[] limit = new LimitSwitch[2];

    private LiftSubsystem()
    {
        RobotConfig config = RobotConfig.getInstance();
        kTuning = config.LIFT_DEBUG;
        kSlotUp = config.LIFT_CLOSED_LOOP.gains.get(0).kSlot;
        kSlotDown = config.LIFT_CLOSED_LOOP.gains.get(1).kSlot;;

        List<TalonSRX> talons = TalonFactory.makeClosedLoopTalon(config.LIFT_CLOSED_LOOP);

        for (int index = 0; index < motors.length; index++)
        {
            motors[index] = talons.get(index);
            motors[index].setSelectedSensorPosition(config.LIFT_TICKS_STARTING, 0, 10);
            motors[index].configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, 10);
            motors[index].configVelocityMeasurementWindow(32, 10);
            motors[index].configNeutralDeadband(0.1, 10);

            limit[index] =  new LimitSwitch(motors[index], config.LIFT_LIMIT_SWITCH_NORMALLY[index][0], config.LIFT_LIMIT_SWITCH_NORMALLY[index][1]);
        }
    }

    public static LiftSubsystem getInstance()
    {
        if (instance == null)
        {
            instance = new LiftSubsystem();
        }
        return instance;
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new LiftAutoZeroThenPercentVoltage());
        //        setDefaultCommand(new LiftPercentVoltage());
    }

    public void updateSensors()
    {
        if(LimitSwitch.atReverseAll(limit))
        {
            motors[0].setSelectedSensorPosition(0, 0, 0);
            motors[1].setSelectedSensorPosition(0, 0, 0);
        }

        state.updateLiftHeight(ticksToInches(getHeightAverage()));
    }

    public void reportState()
    {
        boolean[][] limitSwitchSet = LimitSwitch.dump(limit);

        SmartDashboard.putNumber("Lift Height", ticksToInches(getHeightAverage()));
        SmartDashboard.putBoolean("LB", limitSwitchSet[0][0]);
        SmartDashboard.putBoolean("LT", limitSwitchSet[0][1]);
        SmartDashboard.putBoolean("RB", limitSwitchSet[1][0]);
        SmartDashboard.putBoolean("RT", limitSwitchSet[1][1]);

        if (kTuning)
        {
            debugCalibration();
        }
    }

    public double getLiftHeight()
    {
        return ticksToInches(motors[0].getSelectedSensorPosition(0)) ;
    }
    public boolean allSidesAtBottom()
    {
        return LimitSwitch.atReverseAll(limit);
    }

    public boolean allSidesAtTop()
    {
        return LimitSwitch.atForwardAll(limit);
    }
    public boolean eitherSideAtTop()
    {
        return LimitSwitch.atForwardAny(limit);
    }

    public synchronized void setClosedLoop(double heightInches)
    {
        final int slot = (heightInches > getHeightAverage()) ? kSlotUp : kSlotDown;

        motors[0].selectProfileSlot(slot, 0);
        motors[1].selectProfileSlot(slot, 0);
        setMotor(ControlMode.MotionMagic, inchesToTicks(heightInches));  // Stalls motors
    }

    public synchronized void setOpenLoop(double percent)
    {
        double speed = percent;
        if (LimitSwitch.atForwardAny(limit) && speed > 0.0)
        {
            speed = 0.0;
        }
        setMotor(ControlMode.PercentOutput, percent);
    }

    private double getHeightAverage()
    {
        return (motors[0].getSelectedSensorPosition(0) + motors[1].getSelectedSensorPosition(0)) / 2.0;
    }

    private static double inchesToTicks(double inches)
    {
        return inches * kRevsPerInch * kTicksPerRev;
    }

    private double ticksToInches(double ticks)
    {
        return ticks / kRevsPerInch / kTicksPerRev;
    }

    private void setMotor(ControlMode mode, double val)
    {
        final double feedforward = (state.robotHasCube) ? kFeedForwardHasCube : kFeedForwardNoCube;

        motors[0].set(mode, val, DemandType.ArbitraryFeedForward, feedforward);
        motors[1].set(mode, val, DemandType.ArbitraryFeedForward, feedforward);
    }

    private void debugCalibration()
    {
        double position0 = motors[0].getSelectedSensorPosition(0);
        double position1 = motors[1].getSelectedSensorPosition(0);
        double error0 = motors[0].getClosedLoopError(0);
        double error1 = motors[1].getClosedLoopError(0);

        SmartDashboard.putNumber("Position0", position0);
        SmartDashboard.putNumber("Position1", position1);
        SmartDashboard.putNumber("Velocity", motors[0].getSelectedSensorVelocity(0));

        SmartDashboard.putNumber("Error0", error0);
        SmartDashboard.putNumber("Error1", error1);

        SmartDashboard.putNumber("Delta P", position0 - position1);

        double p = Preferences.getInstance().getDouble("lift_p", 0.0);
        double i = Preferences.getInstance().getDouble("lift_i", 0.0);
        double d = Preferences.getInstance().getDouble("lift_d", 0.0);
        int slot = Preferences.getInstance().getInt("lift_slot", 0);

        motors[0].config_kP(slot, p, 10);
        motors[1].config_kP(slot, p, 10);
        motors[0].config_kI(slot, i, 10);
        motors[1].config_kI(slot, i, 10);
        motors[0].config_kD(slot, d, 10);
        motors[1].config_kD(slot, d, 10);
    }

    public boolean runFunctionalTest()
    {
        return true;
    }
}
