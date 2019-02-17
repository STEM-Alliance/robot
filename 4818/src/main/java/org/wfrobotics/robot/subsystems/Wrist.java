package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.wrist.WristOpenLoop;
import org.wfrobotics.robot.commands.wrist.WristZeroThenOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Wrist extends PositionBasedSubsystem
{
    public static Wrist getInstance()
    {
        if (instance == null)
        {
            instance = new Wrist(RobotConfig.getInstance().getWristConfig());
        }
        return instance;
    }

    private static Wrist instance = null;

    public Wrist(PositionConfig positionConfig)
    {
        super(positionConfig);
        master.configOpenloopRamp(.15, 100);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristOpenLoop());
    }

    @Override
    public void reportState()
    {
        super.reportState();
        SmartDashboard.putBoolean("Cargo Mode", inCargoMode());
        SmartDashboard.putBoolean("Hatch Mode", inHatchMode());
    }

    // TODO Override setOpenLoop and setClosedLoop, making sure the poppers aren't extended
    //      if we move the motor

    public boolean inCargoMode()
    {
        final double angle = getPosition();
        return 0.0 <= angle && angle <= 360.0;  // TODO tell if angle is "ready" for this
    }

    public boolean inHatchMode()
    {
        final double angle = getPosition();
        return angle >= kFullRangeInchesOrDegrees * 0.8;
    }

    public boolean isCloserToCargoModeThanHatchMode()
    {
        final double angle = getPosition();
        return angle < kFullRangeInchesOrDegrees / 2.0;
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();
        ClosedLoopConfig config = RobotConfig.getInstance().getWristConfig().kClosedLoop;

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkClosedLoopConfig(config));
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));
        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}
