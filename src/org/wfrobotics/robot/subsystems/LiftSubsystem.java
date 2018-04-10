package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.subsystems.Subsystem;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.lift.LiftAutoZeroThenPercentVoltage;
import org.wfrobotics.robot.config.RobotMap;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LiftSubsystem extends Subsystem
{
    private final LimitSwitchs limit;
    private final static double kTicksPerRev = 4096.0;
    private final static double kRevsPerInch = 1.0 / 4.555;  // Measured on practice robot
    private final int kSlotUp = 0;
    private final int kSlotDown = 0;
    private final boolean kDebug;

    private final RobotState state = RobotState.getInstance();
    private final TalonSRX[] motors = new TalonSRX[2];

    public LiftSubsystem(RobotConfig config)
    {
        final int kTimeout = 10;
        final int[] addresses =  {RobotMap.CAN_LIFT_L, RobotMap.CAN_LIFT_R};
        final boolean[] inverted = {config.LIFT_MOTOR_INVERTED_LEFT, config.LIFT_MOTOR_INVERTED_RIGHT};
        final boolean[] sensorPhase = {config.LIFT_SENSOR_PHASE_LEFT, config.LIFT_SENSOR_PHASE_LEFT};

        kDebug = config.LIFT_DEBUG;

        for (int index = 0; index < motors.length; index++)
        {
            motors[index] = TalonSRXFactory.makeConstAccelControlTalon(addresses[index], config.LIFT_P[kSlotUp], config.LIFT_I[kSlotUp], config.LIFT_D[kSlotUp], config.LIFT_F[kSlotUp], kSlotUp, config.LIFT_VELOCITY[kSlotUp], config.LIFT_ACCELERATION[kSlotUp]);
            motors[index].config_IntegralZone(kSlotUp, 20, kTimeout);
            motors[index].set(ControlMode.PercentOutput, 0);
            motors[index].setInverted(inverted[index]);
            motors[index].setSensorPhase(sensorPhase[index]);
            motors[index].setNeutralMode(NeutralMode.Brake);
            motors[index].setSelectedSensorPosition(config.LIFT_TICKS_STARTING, 0, kTimeout);
            motors[index].configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, kTimeout);
            motors[index].configVelocityMeasurementWindow(32, kTimeout);
            motors[index].setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, kTimeout);
            motors[index].setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, kTimeout);
            if (config.LIFT_DEBUG)
            {
                motors[index].setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 5, kTimeout);  // For calibration
            }
        }
        // Down gains
        for (int index = 0; index < motors.length; index++)
        {
            motors[index].config_IntegralZone(kSlotDown, 20, kTimeout);
            motors[index].config_kF(kSlotDown, config.LIFT_F[kSlotDown], kTimeout);
            motors[index].config_kP(kSlotDown, config.LIFT_P[kSlotDown], kTimeout);
            motors[index].config_kI(kSlotDown, config.LIFT_I[kSlotDown], kTimeout);
            motors[index].config_kD(kSlotDown, config.LIFT_D[kSlotDown], kTimeout);
        }

        limit = new LimitSwitchs(motors, config.LIFT_LIMIT_SWITCH_NORMALLY);
    }

    // ----------------------------------------- Interfaces ----------------------------------------

    public void initDefaultCommand()
    {
        setDefaultCommand(new LiftAutoZeroThenPercentVoltage());
        //        setDefaultCommand(new LiftPercentVoltage());
    }

    public void updateSensors()
    {
        if(limit.atBottomAll())
        {
            motors[0].setSelectedSensorPosition(0, 0, 0);
            motors[1].setSelectedSensorPosition(0, 0, 0);
        }

        state.updateLiftHeight(ticksToInches(getHeightAverage()));
    }

    public void reportState()
    {
        boolean[][] limitSwitchSet = limit.dump();

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
        return limit.atBottomAll();
    }

    public boolean allSidesAtTop()
    {
        return limit.atTopAll();
    }
    public boolean eitherSideAtTop()
    {
        return limit.atTopAny();
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
        if (limit.atTopAny() && speed > 0.0)
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

        double p = Preferences.getInstance().getDouble("lift_p_0", 0.0);
        double i = Preferences.getInstance().getDouble("lift_i_0", 0.0);
        double d = Preferences.getInstance().getDouble("lift_d_0", 0.0);

        motors[0].config_kP(0, p, 10);
        motors[1].config_kP(0, p, 10);
        motors[0].config_kI(0, i, 10);
        motors[1].config_kI(0, i, 10);
        motors[0].config_kD(0, d, 10);
        motors[1].config_kD(0, d, 10);

        p = Preferences.getInstance().getDouble("lift_p_1", 0.0);
        i = Preferences.getInstance().getDouble("lift_i_1", 0.0);
        d = Preferences.getInstance().getDouble("lift_d_1", 0.0);

        motors[0].config_kP(1, p, 10);
        motors[1].config_kP(1, p, 10);
        motors[0].config_kI(1, i, 10);
        motors[1].config_kI(1, i, 10);
        motors[0].config_kD(1, d, 10);
        motors[1].config_kD(1, d, 10);
    }
}
