package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
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

    public Wrist(PositionConfig positionConfig)
    {
        super(positionConfig);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristOpenLoop());
    }

    public boolean inCargoMode()
    {
        final double angle = getPosition();
        return 0.0 <= angle && angle <= 360.0;  // TODO tell if angle is "ready" for this
    }

    public boolean inHatchMode()
    {
        final double angle = getPosition();
        return 0.0 <= angle && angle <= 360.0;  // TODO tell if angle is "ready" for this
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));
        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}
