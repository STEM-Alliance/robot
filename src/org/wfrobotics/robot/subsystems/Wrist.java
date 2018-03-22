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

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Wrist extends Subsystem
{
    private final int kTicksToTop;
    private final boolean kZeroAtBottom;

    private final RobotState state = RobotState.getInstance();
    private final TalonSRX intakeLift;

    public Wrist(RobotConfig config)
    {
        kTicksToTop = Robot.config.INTAKE_TICKS_TO_TOP;
        kZeroAtBottom = config.WRIST_ZERO_BOTTOM;

        intakeLift = TalonSRXFactory.makeConstAccelControlTalon(RobotMap.CAN_INTAKE_LIFT, config.INTAKE_P, config.INTAKE_I, config.INTAKE_D, config.INTAKE_F, 0, config.INTAKE_MAX_POSSIBLE_UP, config.INTAKE_ACCELERATION);
        intakeLift.setNeutralMode(NeutralMode.Brake);
        //        intakeLift.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);
        //        intakeLift.configForwardSoftLimitThreshold(config.INTAKE_TICKS_TO_TOP, 10);
        intakeLift.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 10);
        intakeLift.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 10);
        intakeLift.configSetParameter(ParamEnum.eClearPositionOnLimitF, (kZeroAtBottom) ? 0 : 1, 0, 0, 10);
        intakeLift.configSetParameter(ParamEnum.eClearPositionOnLimitR, (kZeroAtBottom) ? 1 : 0, 0, 0, 10);
        intakeLift.overrideLimitSwitchesEnable(true);
        intakeLift.setSelectedSensorPosition((kZeroAtBottom) ? 9999 : -9999, 0, 10);  // Before zeroing, report values above smart intake active therehold
        intakeLift.configOpenloopRamp(.05, 10);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristAutoZeroThenPercentVoltage());
        //        setDefaultCommand(new IntakeLift());
    }

    public boolean intakeLiftAtBottom()
    {
        return intakeLift.getSensorCollection().isRevLimitSwitchClosed();
    }

    public boolean intakeLiftAtTop()
    {
        return intakeLift.getSensorCollection().isFwdLimitSwitchClosed();
    }

    public void setIntakeLiftSpeed(double percentageUp)
    {
        intakeLift.set(ControlMode.PercentOutput, percentageUp);
    }

    /**
     * sets the intake position using motion magic
     * @param percentUp The amount to go up, 0 to 1 for down to up, respectively
     */
    public void setIntakeLiftPosition(double percentUp)
    {
        final double ticks = (kZeroAtBottom) ? percentUp * kTicksToTop : (1.0 - percentUp) * kTicksToTop;
        intakeLift.set(ControlMode.MotionMagic, ticks);
    }

    public void reportState()
    {
        double ticks = intakeLift.getSelectedSensorPosition(0);
        //        SmartDashboard.putNumber("Intake Lift Error", intakeLift.getClosedLoopError(0));
        //        SmartDashboard.putNumber("Intake Lift Position", ticks);
        //        SmartDashboard.putNumber("Intake Lift Velocity", intakeLift.getSelectedSensorVelocity(0));
        SmartDashboard.putBoolean("Wrist LimitB", intakeLiftAtBottom());
        SmartDashboard.putBoolean("Wrist LimitT", intakeLiftAtTop());
        state.updateWristPosition(ticks);
    }
}
