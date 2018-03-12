package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.ExampleStopCommand;
import org.wfrobotics.prototype.config.RobotConfig;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ExampleSubsystem extends Subsystem
{
    private class PointStreamer implements Runnable
    {
        public void run()
        {
            onBackgroundUpdate();
        }
    }

    private final TalonSRX motor;
    private final PathFollower pathFollower;
    private final MotionProfileStatus status = new MotionProfileStatus();
    private final Notifier service = new Notifier(new PointStreamer());
    private boolean onTarget = false;

    public ExampleSubsystem(RobotConfig config)
    {
        int addToEachTrajDuration = 0;
        int kRobotPeriodHalf = 10;
        int kRobotPeriodQuarter = 5;

        motor = TalonSRXFactory.makeTalon(1);

        // Example
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);
        int abs = motor.getSensorCollection().getPulseWidthPosition();
        motor.setSelectedSensorPosition(abs, 0, 10);
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);

        motor.setInverted(true);
        motor.setSensorPhase(true);
        motor.configNeutralDeadband(.01, 10);  // TODO Port to drive train
        // TODO Delete Lazy Talon

        motor.selectProfileSlot(0, 0);
        motor.config_kP(0, config.kP, 10);
        motor.config_kI(0, config.kI, 10);
        motor.config_kD(0, config.kD, 10);
        motor.config_kF(0, 1023.0 / config.maxVelocity, 10);

        motor.configMotionProfileTrajectoryPeriod(addToEachTrajDuration, 10);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, kRobotPeriodHalf, 10);
        motor.changeMotionControlFramePeriod(kRobotPeriodQuarter);

        // Additions
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, kRobotPeriodQuarter, 10);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, kRobotPeriodQuarter, 10);

        motor.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, 10);
        motor.configVelocityMeasurementWindow(32, 10);

        motor.set(ControlMode.PercentOutput, 0);

        // Defaults
        pathFollower = new PathFollower();
        service.startPeriodic(.005);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ExampleStopCommand());
    }

    public synchronized void onBackgroundUpdate()
    {
        SmartDashboard.putNumber("Streamer Time", Timer.getFPGATimestamp());
        if (motor.getControlMode() == ControlMode.MotionProfile)  // TODO Could track ourselves, would remove call to hardware in BU
        {
            motor.processMotionProfileBuffer();
        }
    }

    public void driveDifferential(double speed)
    {
        motor.set(ControlMode.PercentOutput, speed);
    }

    public void drivePath()
    {
        // TODO reset distance driven
        motor.set(ControlMode.MotionProfile, SetValueMotionProfile.Disable.value);  // Disable before modifying buffer
        motor.clearMotionProfileTrajectories();  // Flush prior path remaining
        pathFollower.reload();
        updatePathBuffer();  // Fill buffer (partially) before enabling
        onTarget = false;

        motor.set(ControlMode.MotionProfile, SetValueMotionProfile.Enable.value);
    }

    public boolean onTarget()
    {
        return onTarget;
    }

    public void reportState()
    {
        ControlMode mode = motor.getControlMode();  // TODO Could track ourselves, would remove false warning on MP done and stay in mode intentionally
        String mode_s = (mode == ControlMode.MotionProfile) ? "Motion Profile" : "Percent Voltage";

        if (mode == ControlMode.MotionProfile)
        {
            motor.getMotionProfileStatus(status);
            if (status.hasUnderrun)
            {
                DriverStation.reportWarning("Motion profile buffer underrun", false);
                motor.clearMotionProfileHasUnderrun(0);
            }

            if (status.topBufferCnt < 1947)  // CTRE: "If the profile is very large (2048 points or more) the function may return a nonzero error code."
            {
                updatePathBuffer();
            }
            if (status.activePointValid && status.isLast)
            {
                motor.set(ControlMode.MotionProfile, SetValueMotionProfile.Hold.value);
                onTarget = true;
            }

            // TODO Put just position and velocity in if(kDebug) in drive train
            SmartDashboard.putNumber("MP Buffer Top Count", status.topBufferCnt);
            SmartDashboard.putNumber("MP Buffer Top Remaining", status.topBufferRem);
            SmartDashboard.putNumber("MP Buffer Bot Count", status. btmBufferCnt);
            SmartDashboard.putNumber("MP Position", motor.getActiveTrajectoryPosition());
            SmartDashboard.putNumber("MP Velocity", motor.getActiveTrajectoryVelocity());
        }

        // TODO Remove in drive train
        SmartDashboard.putNumber("Position", motor.getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Velocity", motor.getSelectedSensorVelocity(0));
        SmartDashboard.putString("Mode", mode_s);
    }

    private void updatePathBuffer()
    {
        TrajectoryPoint[][] t = pathFollower.next(100);  // Less initial delay if streamed

        for (int index = 0; index < t.length; index++)
        {
            if (motor.pushMotionProfileTrajectory(t[index][0]) != ErrorCode.OK)
            {
                DriverStation.reportWarning("Motion profile buffer push error", false);
            }
            // TODO !!! push the other drive train side !!!
        }
    }
}