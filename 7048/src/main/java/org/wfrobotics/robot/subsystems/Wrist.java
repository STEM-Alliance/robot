package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.wrist.WristOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

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

    private Wrist(PositionConfig positionConfig)
    {
        super(positionConfig);

        TalonFactory.configCurrentLimiting(master, 15
        , 25, 200);  // TODO Tune
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristOpenLoop());
        master.configOpenloopRamp(.3, 100);
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
