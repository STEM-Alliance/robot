package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.CommandTemplate;

import com.ctre.phoenix.motorcontrol.StatusFrame;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The elevator consists of two independently connected Mini CIM motors to raise/lower the intake and climber
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public final class PositionBasedSubTemplate extends PositionBasedSubsystem
{
    private static PositionBasedSubTemplate instance = null;

    public static PositionBasedSubTemplate getInstance()
    {
        if (instance == null)
        {
            // instance = new PositionBasedSubTemplate(RobotConfig.getInstance().getPositionConfig());
        }
        return instance;
    }

    private static final int kTickRateBrakeModeObserved = 400;  // Observed on practice bot March 20th
    private static final int kTickRateSlowEnough = kTickRateBrakeModeObserved + 200;

    private PositionBasedSubTemplate(PositionConfig positionConfig)
    {
        super(positionConfig);
        //        master.configClosedloopRamp(0.15, 100);  // Soften reaching setpoint TODO Tune
        master.configOpenloopRamp(.15, 100);  // TODO Tune because we switched to miniCIMs
        master.setSelectedSensorPosition((int) (positionConfig.kTicksToTop * 2.0), 0, 100);  // Always be able to zero
        master.setStatusFramePeriod(StatusFrame.Status_1_General, 10, 100);  // Faster for limit switch
        TalonFactory.configCurrentLimiting(master, 25, 30, 20);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new CommandTemplate());
    }

    /** Velocity slow enough. Use to improve isFinished() criteria for closed loop commands */
    public boolean onTarget()
    {
        return Math.abs(getVelocityNative()) < kTickRateSlowEnough;
    }


    @Override
    public void zeroIfAtLimit()
    {
        if(AtHardwareLimitBottom())
        {
            zeroEncoder();
        }
    }
    public void reportState()
    {
        SmartDashboard.putString("Position Sub", getCurrentCommandName());
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));

        return report;
    }
}
