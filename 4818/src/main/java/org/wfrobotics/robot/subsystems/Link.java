package org.wfrobotics.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.link.LinkZeroThenOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

/** @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead */
public final class Link extends PositionBasedSubsystem
{
    public static Link getInstance()
    {
        if (instance == null)
        {
            instance = new Link(RobotConfig.getInstance().getLinkConfig());
        }
        return instance;
    }
    
    private static final int kTickRateBrakeModeObserved = 0;  // TODO Tune
    private static final int kTickRateSlowEnough = kTickRateBrakeModeObserved + 200;  // TODO Tune

    private static Link instance = null;

    private Link(PositionConfig positionConfig)
    {
        super(positionConfig);

        master.configOpenloopRamp(0.5, 100);
        master.setControlFramePeriod(ControlFrame.Control_3_General, 10);  // Slow down, responsiveness not critical
        // master.configPeakOutputForward(.33);
        // master.configPeakOutputForward(-.33);
        TalonFactory.configCurrentLimiting(master, 15, 25, 30);

        // TODO Try using Status_10_MotionMagic to improve motion?
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new LinkZeroThenOpenLoop());
    }
    
    @Override
    public void setClosedLoop(double positionSetpoint)
    {
        master.configVoltageCompSaturation(10.0);  // TODO DO we want this larger so we move faster?
        setMotor(ControlMode.MotionMagic, PositionToNative(positionSetpoint));
    }

    @Override
    public void setOpenLoop(double percent)
    {
        master.configVoltageCompSaturation(10.0);
        setMotor(ControlMode.PercentOutput, percent);
    }
    
    public void holdAtHeight(double position)
    {
        master.configVoltageCompSaturation(4.0);
        setMotor(ControlMode.MotionMagic, PositionToNative(position));
    }

    /** Velocity slow enough. Use to improve isFinished() criteria for closed loop commands */
    public boolean onTarget()
    {
        return true;  // TODO will improve performance
        //return Math.abs(getVelocityNative()) < kTickRateSlowEnough;
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();
        ClosedLoopConfig config = RobotConfig.getInstance().getLinkConfig().kClosedLoop;

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkClosedLoopConfig(config));
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));
        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}