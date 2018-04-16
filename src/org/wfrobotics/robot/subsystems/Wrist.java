package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.wrist.WristAutoZeroThenPercentVoltage;
import org.wfrobotics.robot.config.RobotMap;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Wrist extends Subsystem
{
    private final double stallCurrentMin = 15.0;  // TODO Measure
    private final double stallTimeMin = 0.15;

    private final RobotState state = RobotState.getInstance();
    private final TalonSRX intakeLift;

    private boolean stalled;
    private double stallCurrentStarted;
    private boolean debugStalledLatched;

    public Wrist(RobotConfig config)
    {
        intakeLift = TalonSRXFactory.makeConstAccelControlTalon(RobotMap.CAN_INTAKE_LIFT, config.INTAKE_P, config.INTAKE_I, config.INTAKE_D, config.INTAKE_F, 0, 0, config.INTAKE_MAX_POSSIBLE_UP, config.INTAKE_ACCELERATION);
        intakeLift.setNeutralMode(NeutralMode.Brake);
        intakeLift.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 10);
        intakeLift.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 10);
        intakeLift.configSetParameter(ParamEnum.eClearPositionOnLimitF, 0, 0, 0, 10);
        intakeLift.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, 10);
        intakeLift.overrideLimitSwitchesEnable(true);
        intakeLift.setSelectedSensorPosition(0, 0, 10);  // Before zeroing, report values above smart intake active therehold
        intakeLift.configOpenloopRamp(.05, 10);

        stalled = false;
        stallCurrentStarted = Double.POSITIVE_INFINITY;
        debugStalledLatched = false;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristAutoZeroThenPercentVoltage());
    }

    public void updateSensors()
    {
        double position = intakeLift.getSelectedSensorPosition(0);

        if (intakeLift.getOutputCurrent() > stallCurrentMin)
        {
            if (stallCurrentStarted == Double.POSITIVE_INFINITY)
            {
                stallCurrentStarted = Timer.getFPGATimestamp();
            }
            else if (Timer.getFPGATimestamp() - stallCurrentStarted > stallTimeMin)
            {
                stalled = true;
                debugStalledLatched = true;
            }
        }
        else
        {
            stalled = false;
            stallCurrentStarted = Double.POSITIVE_INFINITY;
        }

        if (AtTop())
        {
            intakeLift.setSelectedSensorPosition(Robot.config.INTAKE_TICKS_TO_TOP, 0, 0);
        }

        state.updateWristPosition(position);
    }

    public void reportState()
    {
        double ticks = intakeLift.getSelectedSensorPosition(0);

        SmartDashboard.putString("Wrist", getCurrentCommandName());
        SmartDashboard.putNumber("Intake Lift Position", ticks);
        SmartDashboard.putBoolean("Wrist LimitB", AtBottom());
        SmartDashboard.putBoolean("Wrist LimitT", AtTop());
        SmartDashboard.putBoolean("Wrist Stalled", stalled);
        SmartDashboard.putNumber("Wrist Current", intakeLift.getOutputCurrent());
        SmartDashboard.putBoolean("Wrist Stalled Latched", debugStalledLatched);

        // For Calibration
        //        SmartDashboard.putNumber("Intake Lift Error", intakeLift.getClosedLoopError(0));
        //      SmartDashboard.putNumber("Intake Lift Velocity", intakeLift.getSelectedSensorVelocity(0));
    }

    public boolean AtBottom()
    {
        return intakeLift.getSensorCollection().isRevLimitSwitchClosed();
    }

    public boolean AtTop()
    {
        return intakeLift.getSensorCollection().isFwdLimitSwitchClosed() || stalled;
    }

    public void setSpeed(double percentageUp)
    {
        intakeLift.set(ControlMode.PercentOutput, percentageUp);
    }

    /**
     * Sets the intake position using motion magic
     * @param percentUp The amount to go up, 0 to 1 for down to up, respectively
     */
    public void setPosition(double percentUp)
    {
        intakeLift.set(ControlMode.MotionMagic, percentUp * Robot.config.INTAKE_TICKS_TO_TOP);
    }
}
