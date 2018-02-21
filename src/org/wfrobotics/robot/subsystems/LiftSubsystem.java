package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.background.BackgroundUpdate;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.lift.LiftPercentVoltage;
import org.wfrobotics.robot.config.RobotMap;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LiftSubsystem extends Subsystem implements BackgroundUpdate
{
    private enum LimitSwitch
    {
        BOTTOM,
        TOP
    }

    private final static double kTicksPerRev = 4096;
    private final static double kRevsPerInch = 1 / ( 1.345 * Math.PI * 13.0 / 10.0);;

    private final RobotState state = RobotState.getInstance();
    private final TalonSRX[] motors = new TalonSRX[2];

    private ControlMode desiredMode;
    private double desiredSetpoint;
    private double heightStart;
    private String liftState;
    private double todoRemoveLast;
    private double backgroundPeriod;

    public LiftSubsystem(RobotConfig Config)
    {
        final RobotConfig config = Config;

        final int kTimeout = 10;
        final int kSlot = 0;
        final int[] addresses =  {RobotMap.CAN_LIFT_L, RobotMap.CAN_LIFT_R};
        final boolean[] inverted = {config.LIFT_MOTOR_INVERTED_LEFT, config.LIFT_MOTOR_INVERTED_RIGHT};
        final boolean[] sensorPhase = {config.LIFT_SENSOR_PHASE_LEFT, config.LIFT_MOTOR_INVERTED_RIGHT};
        final int kStartingTicksRelativeToSensor = -1500;

        // TODO Use talon software limit switches

        for (int index = 0; index < motors.length; index++)
        {
            motors[index] = TalonSRXFactory.makeConstAccelControlTalon(addresses[index], config.LIFT_P, config.LIFT_I, config.LIFT_D, config.LIFT_F, kSlot, config.LIFT_VELOCITY, config.LIFT_ACCELERATION);
            motors[index].config_IntegralZone(0, 20, kTimeout);
            motors[index].configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config.LIFT_LIMIT_SWITCH_NORMALLY[index][0], kTimeout);
            motors[index].configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config.LIFT_LIMIT_SWITCH_NORMALLY[index][1], kTimeout);
            motors[index].overrideLimitSwitchesEnable(true);
            motors[index].set(ControlMode.PercentOutput, 0);
            motors[index].setInverted(inverted[index]);
            motors[index].setSensorPhase(sensorPhase[index]);
            motors[index].setNeutralMode(NeutralMode.Brake);
            motors[index].setSelectedSensorPosition(kStartingTicksRelativeToSensor, kSlot, kTimeout);
            motors[index].configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, kTimeout);
            motors[index].configVelocityMeasurementWindow(64, kTimeout);
            motors[index].setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 2, kTimeout);
            motors[index].setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 160, kTimeout);
            //            motors[index].setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 2, kTimeout);  // For calibration
        }

        // Valid initial state
        desiredMode = ControlMode.PercentOutput;
        desiredSetpoint = 0;
        heightStart = 0;
        liftState = "";
        todoRemoveLast = Timer.getFPGATimestamp();
        backgroundPeriod = 0;
    }

    // ----------------------------------------- Interfaces ----------------------------------------

    public void initDefaultCommand()
    {
        setDefaultCommand(new LiftPercentVoltage());
    }

    public synchronized void onBackgroundUpdate()
    {
        final double todoRemoveNow = Timer.getFPGATimestamp();

        if (zeroPositionIfNeeded())
        {
            liftState = "Zeroing";
        }
        else if (goToTransportIfNeeded())
        {
            liftState = "Transport";
        }
        //        else if (applyBrakeAtTarget())
        //        {
        //            liftState = "Brake at target";
        //        }
        else
        {
            liftState = desiredMode.toString();
        }

        set(desiredMode, desiredSetpoint);

        //        debugCalibration();
        backgroundPeriod = todoRemoveNow - todoRemoveLast;
        todoRemoveLast = todoRemoveNow;
    }

    // ----------------------------------------- Public -------------------------------------------

    public boolean allSidesAtBottom()
    {
        return allSidesAtLimitSwitch(LimitSwitch.BOTTOM);
    }

    //    public boolean allSidesAtTop()
    //    {
    //        return allSidesAtLimitSwitch(LimitSwitch.TOP);
    //    }

    public synchronized void goToHeightInit(double heightInches)
    {
        desiredMode = ControlMode.MotionMagic;
        desiredSetpoint = inchesToTicks(heightInches);
        heightStart = getHeightAverage();
    }

    public synchronized void goToSpeedInit(double percent)
    {
        desiredMode = ControlMode.PercentOutput;
        desiredSetpoint = percent;
    }

    public void reportState()
    {
        double height = ticksToInches(getHeightAverage());

        SmartDashboard.putNumber("Lift Height", height);
        SmartDashboard.putString("Lift State", liftState);
        SmartDashboard.putNumber("Background Period", backgroundPeriod * 1000);

        state.updateLiftHeight(height);
    }

    // ----------------------------------------- Private ------------------------------------------

    private boolean goToTransportIfNeeded()
    {
        //        if (state.robotVelocity.getMag() > .5 || state.robotGear)
        //        {
        //            desiredMode = ControlMode.MotionMagic;
        //            desiredSetpoint = inchesToTicks(LiftHeight.Transport.get());
        //            return true;
        //        }
        return false;
    }

    private boolean zeroPositionIfNeeded()
    {
        if(allSidesAtBottom())
        {
            for (int index = 0; index < motors.length; index++)
            {
                motors[index].setSelectedSensorPosition(0, 0, 0);
            }

            //            // Override with valid + safe command
            //            if (desiredMode == ControlMode.MotionMagic && desiredSetpoint < LiftHeight.Intake.get())
            //            {
            //                desiredSetpoint = inchesToTicks(LiftHeight.Intake.get());
            //            }
            //            else if (desiredMode == ControlMode.PercentOutput && desiredSetpoint < 0)
            //            {
            //                desiredSetpoint = 0;
            //            }
            return true;
        }
        return false;
    }

    protected boolean applyBrakeAtTarget()
    {
        if (desiredMode == ControlMode.MotionMagic)
        {
            if (Math.abs(getHeightAverage()) - Math.abs(desiredSetpoint) < Math.abs(heightStart - desiredSetpoint) * .01)
            {
                desiredMode = ControlMode.MotionMagic;
                desiredSetpoint = 0;
                return true;
            }
        }
        return false;
    }

    private void set(ControlMode mode, double val)
    {
        for (int index = 0; index < motors.length; index++)
        {
            motors[index].set(mode, val);
        }
    }

    private double getHeightAverage()
    {
        return (motors[0].getSelectedSensorPosition(0) + motors[1].getSelectedSensorPosition(0)) / 2;
    }

    private boolean allSidesAtLimitSwitch(LimitSwitch limit)
    {
        boolean allAtLimit = true;
        for (int index = 0; index < motors.length; index++)
        {
            allAtLimit &= isSideAtLimit(limit, index);
        }
        return allAtLimit;
    }

    private boolean isSideAtLimit(LimitSwitch limit, int index)
    {
        if(limit == LimitSwitch.BOTTOM)
        {
            return motors[index].getSensorCollection().isRevLimitSwitchClosed();
        }
        return motors[index].getSensorCollection().isFwdLimitSwitchClosed();
    }

    private static double inchesToTicks(double inches)
    {
        return inches * kRevsPerInch * kTicksPerRev;
    }

    private double ticksToInches(double ticks)
    {
        return ticks / kRevsPerInch / kTicksPerRev;
    }

    //    private void debugCalibration()
    //    {
    //        double position0 = motors[0].getSelectedSensorPosition(0);
    //        double position1 = motors[1].getSelectedSensorPosition(0);
    //        double error0 = motors[0].getClosedLoopError(0);
    //        double error1 = motors[1].getClosedLoopError(0);
    //
    //        SmartDashboard.putNumber("Position0", position0);
    //        SmartDashboard.putNumber("Position1", position1);
    //        SmartDashboard.putNumber("Velocity", motors[0].getSelectedSensorVelocity(0));
    //        SmartDashboard.putNumber("TargetPosition", desiredSetpoint);
    //
    //        SmartDashboard.putNumber("Error0", error0);
    //        SmartDashboard.putNumber("Error1", error1);
    //
    //        SmartDashboard.putNumber("Height", ticksToInches(position0));
    //
    //        SmartDashboard.putNumber("Delta E", error0 - error1);
    //        SmartDashboard.putNumber("Delta P", position0 - position1);

    //        SmartDashboard.putBoolean("At Top", allSidesAtTop());
    //        SmartDashboard.putBoolean("At Bottom", allSidesAtBottom());
    //        SmartDashboard.putBoolean("LB", isSideAtLimit(LimitSwitch.BOTTOM, 0));
    //        SmartDashboard.putBoolean("LT", isSideAtLimit(LimitSwitch.TOP, 0));
    //        SmartDashboard.putBoolean("RB", isSideAtLimit(LimitSwitch.BOTTOM, 1));
    //        SmartDashboard.putBoolean("RT", isSideAtLimit(LimitSwitch.TOP, 1));
    //    }

    //    private boolean isAtTop(int index)
    //    {
    //        return isAtLimit(LimitSwitch.Top, index);
    //    }
    //
    //    private boolean isAtBottom(int index)
    //    {
    //        return isAtLimit(LimitSwitch.Bottom, index);
    //    }

    // TODO Beast mode - The fastest lift possible probably dynamically changes it's control strategy to get to it's destination fastest
    //                   This might mean a more aggressive PID (profile) on the way down
    //                   Could go as far as using both closed and open loop control modes
}
