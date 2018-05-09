package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.subsystems.Subsystem;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.lift.LiftAutoZeroThenPercentVoltage;
import org.wfrobotics.robot.config.RobotMap;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LiftSubsystem extends Subsystem
{
    private final static double kTicksPerRev = 4096.0;
    private final static double kRevsPerInch = 1.0 / 4.555;  // Measured on practice robot
    private final int kSlotUp = 0;
    private final int kSlotDown = 1;
    private final boolean kDebug;

    private final RobotState state = RobotState.getInstance();
    private final TalonSRX[] motors = new TalonSRX[2];
    private final LimitSwitch[] limit = new LimitSwitch[2];

    public LiftSubsystem(RobotConfig config)
    {
        final int kTimeout = 10;
        final int[] addresses =  {RobotMap.CAN_LIFT_L, RobotMap.CAN_LIFT_R};
        final boolean[] inverted = {config.LIFT_MOTOR_INVERTED_LEFT, config.LIFT_MOTOR_INVERTED_RIGHT};
        final boolean[] sensorPhase = {config.LIFT_SENSOR_PHASE_LEFT, config.LIFT_SENSOR_PHASE_LEFT};

        kDebug = config.LIFT_DEBUG;

        for (int index = 0; index < motors.length; index++)
        {
            motors[index] = TalonSRXFactory.makeMotionMagicTalon(addresses[index], config.LIFT_P[kSlotUp], config.LIFT_I[kSlotUp], config.LIFT_D[kSlotUp], config.LIFT_F[kSlotUp], 20, kSlotUp, config.LIFT_VELOCITY[kSlotUp], config.LIFT_ACCELERATION[kSlotUp]);
            TalonSRXFactory.configPIDF(motors[index], kSlotDown, config.LIFT_P[kSlotDown], config.LIFT_I[kSlotDown], config.LIFT_D[kSlotDown], config.LIFT_F[kSlotDown], 20);
            motors[index].setInverted(inverted[index]);
            motors[index].setSensorPhase(sensorPhase[index]);
            motors[index].setNeutralMode(NeutralMode.Brake);
            motors[index].setSelectedSensorPosition(config.LIFT_TICKS_STARTING, 0, kTimeout);
            motors[index].configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, kTimeout);
            motors[index].configVelocityMeasurementWindow(32, kTimeout);

            limit[index] =  new LimitSwitch(motors[index], config.LIFT_LIMIT_SWITCH_NORMALLY[index][0], config.LIFT_LIMIT_SWITCH_NORMALLY[index][1]);
        }
    }

    // ----------------------------------------- Interfaces ----------------------------------------

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

        if (kDebug)
        {
            debugCalibration();
        }
    }

    // ----------------------------------------- Public -------------------------------------------

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

    public synchronized void goToHeightInit(double heightInches)
    {
        final int slot = (heightInches > getHeightAverage()) ? kSlotUp : kSlotDown;

        motors[0].selectProfileSlot(slot, 0);
        motors[1].selectProfileSlot(slot, 0);
        set(ControlMode.MotionMagic, inchesToTicks(heightInches));  // Stalls motors
    }

    public synchronized void goToSpeedInit(double percent)
    {
        double speed = percent;
        if (LimitSwitch.atForwardAny(limit) && speed > 0.0)
        {
            speed = 0.0;
        }
        set(ControlMode.PercentOutput, percent);
    }

    // ----------------------------------------- Private ------------------------------------------

    private void set(ControlMode mode, double val)
    {
        motors[0].set(mode, val);
        motors[1].set(mode, val);
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
}
