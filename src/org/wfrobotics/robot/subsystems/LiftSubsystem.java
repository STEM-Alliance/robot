package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.ParallelLift;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.hardware.TalonSRXFactory.TALON_SENSOR;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.ElevateGoHome;
import org.wfrobotics.robot.config.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class LiftSubsystem extends Subsystem
{
    // TODO List of present heights
    // TODO Preset heights in configuration file

    public TalonSRX liftMotorL;
    public TalonSRX liftMotorR;

    public ParallelLift lift;

    private final int liftLoopTimeMsec = 5;
    private final int liftLeaderRateMsec = 20;
    private int liftLoopCounter = 0;

    private final double encoderTicksTotal = 33720;
    private final double travelDistanceInches = 40; // TODO figure this out

    private boolean bottomDetected = false;

    private Notifier loop = new Notifier(() -> {
        liftLoop();
    });

    public LiftSubsystem()
    {
        final int kTimeoutMs = 10;

        // TODO Use Talon factory. If not position control, at least makeTalon()
        liftMotorL = TalonSRXFactory.makeSpeedControlTalon(RobotMap.CAN_LIFT_L, TALON_SENSOR.MAG_ENCODER);
        liftMotorR = TalonSRXFactory.makeSpeedControlTalon(RobotMap.CAN_LIFT_R, TALON_SENSOR.MAG_ENCODER);

        // set the limit switches
        liftMotorL.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, kTimeoutMs);
        liftMotorL.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, kTimeoutMs);
        liftMotorL.overrideLimitSwitchesEnable(true);
        liftMotorL.configForwardSoftLimitEnable(false, 0);
        liftMotorL.configReverseSoftLimitEnable(false, 0);
        //liftMotorL.overrideSoftLimitsEnable(false);

        bottomDetected = false;

        // invert both motors
        liftMotorL.setInverted(true);
        liftMotorR.setInverted(true);

        // since the motors are inverted, we don't invert the phase
        liftMotorL.setSensorPhase(false);
        liftMotorR.setSensorPhase(false);

        liftMotorL.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, (int) encoderTicksTotal, kTimeoutMs);

        // set all the status messages to be super quick
        //        liftMotorL.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 5, kTimeoutMs);
        //        liftMotorL.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 5, kTimeoutMs);
        //        liftMotorL.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 1 , kTimeoutMs);
        //        //LiftMotor.setControlFramePeriod(ControlFrame.Control_3_General, kTimeoutMs);
        //        liftMotorL.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 5, kTimeoutMs);
        //        liftMotorL.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, 5, kTimeoutMs);

        // TODO Setup two hardware managed limit switches - Faster & safer than software limit switches
        //LiftMotor.setNeutralMode(NeutralMode.Brake);

        // initialize the parallel lift using the two talons
        lift = new ParallelLift();
        loop.startPeriodic(liftLoopTimeMsec / 1000.0);

        liftMotorL.setNeutralMode(NeutralMode.Brake);

        updateSmartDashboard();
    }

    // TODO synchronized
    private void liftLoop()
    {
        // only run when enabled, so we don't spin up PID loops
        if(!DriverStation.getInstance().isDisabled())
        {
            // if loop has been ran (Leader) amount of times
            if(liftLoopCounter % (liftLeaderRateMsec / liftLoopTimeMsec) == 0)
            {
                //TODO leaderRun returns the desired speed
                //lift.leaderRun(liftMotorL.getSelectedSensorPosition(0), liftMotorR.getSelectedSensorPosition(0));
                updateSmartDashboard();
            }

            liftLoopCounter++;
        }
        else
        {
            liftLoopCounter = 0;
        }
    }

    private double convertHeightToTicks(double inches)
    {
        return inches * travelDistanceInches * encoderTicksTotal;
    }

    private double convertTicksToHeight(double encoderTicks)
    {
        return encoderTicks / encoderTicksTotal * travelDistanceInches;
    }

    private double getCurrentHeight()
    {
        return convertTicksToHeight(liftMotorL.getSelectedSensorPosition(0));
    }

    // TODO There's a "state pattern" that can help us if the rules for going to/from each state gets too complex
    public void goToPosition(double destination)
    {
        // TODO Setup two hardware managed limit switches - Faster & safer than software limit switches
        liftMotorL.setNeutralMode(NeutralMode.Coast);

        // tell the parallel lift to use a new setpoint
        lift.setDesiredHeightInTicks(convertHeightToTicks(destination));
    }

    // TODO Lift needs to hold position by default
    //      Beast mode - Can (or should we even) automatically go the height based on if we have a cube or some IO to tell our intended preset to score on?
    public void initDefaultCommand()
    {
        setDefaultCommand(new ElevateGoHome());
    }

    public void setSpeed(double speed)
    {
        double output = speed;

        //        if(isAtBottom() && speed < 0 || isAtTop() && speed > 0)
        //        {
        //            output = 0;
        //        }

        liftMotorL.set(ControlMode.PercentOutput, output);

        zeroPositionIfNeeded();
        updateSmartDashboard();
    }

    private void updateSmartDashboard()
    {
        SmartDashboard.putNumber("LiftVoltageL", liftMotorL.getMotorOutputVoltage());
        SmartDashboard.putNumber("LiftHeight", getCurrentHeight());

        SmartDashboard.putNumber("LeaderError", lift.leaderError);
        SmartDashboard.putNumber("LeaderOutput", lift.leaderOutput);
        SmartDashboard.putBoolean("LiftAtTop", isAtTop());
        SmartDashboard.putBoolean("LiftAtBottom", isAtBottom());

        SmartDashboard.putNumber("LiftEncoderTicks", getEncoder());
        SmartDashboard.putBoolean("LiftDetectedBottom", bottomDetected);
    }

    public void zeroPositionIfNeeded()
    {
        if(Robot.liftSubsystem.isAtBottomLimitSwitch())
        {
            Robot.liftSubsystem.liftMotorL.setSelectedSensorPosition(0, 0, 0);
        }
    }

    public boolean isAtTop()
    {
        return !liftMotorL.getSensorCollection().isFwdLimitSwitchClosed();
    }

    private boolean isAtBottomLimitSwitch()
    {
        return !liftMotorL.getSensorCollection().isRevLimitSwitchClosed();
    }

    public boolean isAtBottom()
    {
        // check hard and soft limit switches
        return isAtBottomLimitSwitch();// || getEncoder() <= 100;
    }

    public int getEncoder()
    {
        return liftMotorL.getSelectedSensorPosition(0);
    }

    // TODO Report fommatted state to RobotState. Not the height, but instead something like what the Robot can do. Ex: isSafeToExhaustScale

    // TODO Automatically zero whenever we pass by that sensor(s)

    // TODO What's the most automatic way we can score on the first layer of cube (on scale/switch) vs the second? What are the easiest xbox controls for that?

    // TODO Beast mode - The fastest lift possible probably dynamically changes it's control strategy to get to it's destination fastest
    //                   This might mean a more aggressive PID (profile) on the way down
    //                   Could go as far as using both closed and open loop control modes
}
