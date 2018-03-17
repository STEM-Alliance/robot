package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.background.BackgroundUpdate;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
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
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LiftSubsystem extends Subsystem implements BackgroundUpdate
{
    private final LimitSwitchs limit;
    private final static double kTicksPerRev = 4096.0;
    private final static double kRevsPerInch = 1.0 / 4.555;  // Measured on practice robot
    private final boolean kDebug;

    private final RobotState state = RobotState.getInstance();
    private final TalonSRX[] motors = new TalonSRX[2];

    private double desiredSetpoint;
    private double todoRemoveLast;
    private double backgroundPeriod;
    private boolean AtTopLimitL;

    public LiftSubsystem(RobotConfig config)
    {
        final int kTimeout = 10;
        final int kSlot = 0;
        final int[] addresses =  {RobotMap.CAN_LIFT_L, RobotMap.CAN_LIFT_R};
        final boolean[] inverted = {config.LIFT_MOTOR_INVERTED_LEFT, config.LIFT_MOTOR_INVERTED_RIGHT};
        final boolean[] sensorPhase = {config.LIFT_SENSOR_PHASE_LEFT, config.LIFT_SENSOR_PHASE_LEFT};

        kDebug = config.LIFT_DEBUG;

        for (int index = 0; index < motors.length; index++)
        {
            motors[index] = TalonSRXFactory.makeConstAccelControlTalon(addresses[index], config.LIFT_P, config.LIFT_I, config.LIFT_D, config.LIFT_F, kSlot, config.LIFT_VELOCITY, config.LIFT_ACCELERATION);
            motors[index].config_IntegralZone(0, 0, kTimeout);
            motors[index].set(ControlMode.PercentOutput, 0);
            motors[index].setInverted(inverted[index]);
            motors[index].setSensorPhase(sensorPhase[index]);
            motors[index].setNeutralMode(NeutralMode.Brake);
            motors[index].setSelectedSensorPosition(config.LIFT_TICKS_STARTING, kSlot, kTimeout);
            motors[index].configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, kTimeout);
            motors[index].configVelocityMeasurementWindow(32, kTimeout);
            motors[index].setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, kTimeout);
            motors[index].setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, kTimeout);
            if (config.LIFT_DEBUG)
            {
                motors[index].setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 5, kTimeout);  // For calibration
            }
        }
        limit = new LimitSwitchs(motors, config.LIFT_LIMIT_SWITCH_NORMALLY);

        desiredSetpoint = 0.0;
        todoRemoveLast = Timer.getFPGATimestamp();
        backgroundPeriod = 0.0;
        AtTopLimitL = false;
    }

    // ----------------------------------------- Interfaces ----------------------------------------

    public void initDefaultCommand()
    {
        setDefaultCommand(new LiftAutoZeroThenPercentVoltage());
        //        setDefaultCommand(new LiftPercentVoltage());
    }

    public synchronized void onBackgroundUpdate()
    {
        final double todoRemoveNow = Timer.getFPGATimestamp();

        if(limit.atBottomAll())
        {
            motors[0].setSelectedSensorPosition(0, 0, 0);
            motors[1].setSelectedSensorPosition(0, 0, 0);
        }

        AtTopLimitL = limit.atTopAny();//isSideAtLimit(LimitSwitch.TOP, 0);

        //        if (Math.abs(getHeightAverage() - desiredSetpoint) < 2)
        //        {
        //            motors[0].setIntegralAccumulator(0, 0, 0);
        //            motors[1].setIntegralAccumulator(0, 0, 0);
        //        }

        backgroundPeriod = todoRemoveNow - todoRemoveLast;
        todoRemoveLast = todoRemoveNow;
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

    public synchronized void goToHeightInit(double heightInches)
    {
        set(ControlMode.MotionMagic, inchesToTicks(heightInches));  // Stalls motors
    }

    public synchronized void goToSpeedInit(double percent)
    {
        if (!AtTopLimitL)
        {
            set(ControlMode.PercentOutput, percent);
        }
    }

    public void reportState()
    {
        double height = ticksToInches(getHeightAverage());
        boolean[][] limitSwitchSet = limit.dump();

        if (AtTopLimitL)
        {
            set(ControlMode.PercentOutput, 0);
        }

        SmartDashboard.putNumber("Lift Height", height);
        SmartDashboard.putNumber("Background Period", backgroundPeriod * 1000.0);
        SmartDashboard.putBoolean("LB", limitSwitchSet[0][0]);
        SmartDashboard.putBoolean("LT", limitSwitchSet[0][1]);
        SmartDashboard.putBoolean("RB", limitSwitchSet[1][0]);
        SmartDashboard.putBoolean("RT", limitSwitchSet[1][1]);
        if (kDebug)
        {
            debugCalibration();
        }

        state.updateLiftHeight(height);
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

    /*private boolean allSidesAtLimitSwitch(LimitSwitch limit)
    {
        boolean allAtLimit = true;
        for (int index = 0; index < motors.length; index++)
        {
            allAtLimit &= isSideAtLimit(limit, index);
        }
        return allAtLimit;
    }*/

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
        SmartDashboard.putNumber("TargetPosition", desiredSetpoint);

        SmartDashboard.putNumber("Error0", error0);
        SmartDashboard.putNumber("Error1", error1);

        SmartDashboard.putNumber("Height", ticksToInches(getHeightAverage()));

        SmartDashboard.putNumber("Delta E", error0 - error1);
        SmartDashboard.putNumber("Delta P", position0 - position1);

        double p = Preferences.getInstance().getDouble("lift_p", 0.0);
        double i = Preferences.getInstance().getDouble("lift_i", 0.0);
        double d = Preferences.getInstance().getDouble("lift_d", 0.0);

        motors[0].config_kP(0, p, 10);
        motors[1].config_kP(0, p, 10);
        motors[0].config_kI(0, i, 10);
        motors[1].config_kI(0, i, 10);
        motors[0].config_kD(0, d, 10);
        motors[1].config_kD(0, d, 10);
    }
}
