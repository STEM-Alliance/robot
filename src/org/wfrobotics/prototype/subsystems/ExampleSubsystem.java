package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.ExampleStopCommand;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.reuse.utilities.Utilities;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;
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
            SmartDashboard.putNumber("Streamer Time", Timer.getFPGATimestamp());
            motor.processMotionProfileBuffer();
        }
    }

    private final int maxVelocityTicks = 6000;
    private TalonSRX motor;
    private MotionProfileStatus status = new MotionProfileStatus();
    private Notifier service = new Notifier(new PointStreamer());
    private boolean isDone = false;

    public ExampleSubsystem()
    {
        int addToEachTrajDuration = 0;
        int kHalfRobotPeriod = 10;

        motor = TalonSRXFactory.makeTalon(1);

        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);
        int abs = motor.getSensorCollection().getPulseWidthPosition();
        motor.setSelectedSensorPosition(abs, 0, 10);

        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        motor.setInverted(true);
        motor.setSensorPhase(true);
        motor.configNeutralDeadband(.01, 10);

        motor.selectProfileSlot(0, 0);
        motor.config_kP(0, 0.0, 10);
        motor.config_kI(0, 0.0, 10);
        motor.config_kD(0, 0.0, 10);
        motor.config_kF(0, 1023.0 / maxVelocityTicks, 10);

        motor.configMotionProfileTrajectoryPeriod(addToEachTrajDuration, 10);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, kHalfRobotPeriod, 10);
        motor.changeMotionControlFramePeriod(kHalfRobotPeriod / 2);

        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, 10);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 5, 10);

        motor.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_1Ms, 10);
        motor.configVelocityMeasurementWindow(1, 10);

        service.startPeriodic(.005);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ExampleStopCommand());
    }

    public void manual(double speed)
    {
        motor.set(ControlMode.PercentOutput, speed);
    }

    public void start()
    {
        motor.set(ControlMode.MotionProfile, SetValueMotionProfile.Disable.value);  // Disable before modifying buffer
        load();
        isDone = false;

        motor.getMotionProfileStatus(status);
        // TODO if not preloading all points, enable only after min points in buffer on status object
        motor.set(ControlMode.MotionProfile, SetValueMotionProfile.Enable.value);
    }

    public void update()
    {
        String mode;

        if (motor.getControlMode() == ControlMode.MotionProfile)
        {
            motor.getMotionProfileStatus(status);

            SmartDashboard.putBoolean("MP Underrun", status.hasUnderrun);
            if (status.hasUnderrun)
            {
                motor.clearMotionProfileHasUnderrun(0);
            }
            // TODO fill more points if needed

            SmartDashboard.putBoolean("MP Is Last", status.isLast);
            SmartDashboard.putNumber("MP Buffer Top Count", status.topBufferCnt);
            SmartDashboard.putNumber("MP Buffer Top Remaining", status.topBufferRem);
            SmartDashboard.putNumber("MP Buffer Bot Count", status. btmBufferCnt);
            SmartDashboard.putNumber("MP Position", motor.getActiveTrajectoryPosition());
            SmartDashboard.putNumber("MP Velocity", motor.getActiveTrajectoryVelocity());

            if (status.activePointValid && status.isLast)
            {
                motor.set(ControlMode.MotionProfile, SetValueMotionProfile.Hold.value);
                isDone = true;
            }

            mode = "Motion Profile";
        }
        else
        {
            mode = "Percent Voltage";
        }

        SmartDashboard.putNumber("Position", motor.getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Velocity", motor.getSelectedSensorVelocity(0));
        SmartDashboard.putString("Mode", mode);
    }

    public boolean isDone()
    {
        return isDone;
    }

    private void load()
    {
        double[][] points = generate();
        double numPoints = points.length;
        double lastIndex = numPoints - 1;

        if (numPoints > 2047)
        {
            DriverStation.reportWarning("Would overflow top buffer", true);
        }

        motor.clearMotionProfileTrajectories();

        // TODO stream over time, check if buffer is full on status

        for (int index = 0; index < numPoints; index++)
        {
            TrajectoryPoint buffer = new TrajectoryPoint();

            buffer.position = points[index][0];
            buffer.velocity = points[index][1];
            buffer.profileSlotSelect0 = 0;
            buffer.profileSlotSelect1 = 0;
            buffer.timeDur = TrajectoryDuration.Trajectory_Duration_10ms;
            buffer.zeroPos = index == 0;  // Zeros position
            buffer.isLastPoint = index == lastIndex;

            motor.pushMotionProfileTrajectory(buffer);
        }
    }

    private double[][] generate()
    {
        int numPoints = 1000;
        double maxRevs = 5;
        double maxTicksPer100Ms = maxVelocityTicks;
        double[][] points = new double[numPoints][2];

        for (int index = 0; index < numPoints; index++)
        {
            double ticks = Utilities.scaleToRange(index, 0, numPoints, 0, maxRevs * 4096);
            double unitsPer100Ms = Utilities.scaleToRange((numPoints/2-(Math.abs(numPoints/2-index)))*2.0, 0, numPoints, 0, maxTicksPer100Ms);
            points[index][0] = ticks;
            points[index][1] = unitsPer100Ms;
        }
        return points;
    }
}