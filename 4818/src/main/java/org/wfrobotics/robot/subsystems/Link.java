package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.link.LinkOpenLoop;
import org.wfrobotics.robot.commands.link.LinkZeroThenOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

/**
 * The wrist consists of a BAG motor to rotate the intake
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class Link extends PositionBasedSubsystem
{
    public static Link getInstance()
    {
        if (instance == null)
        {
            instance = new Link(RobotConfig.getInstance().getLinkConfig());
        }
        return instance;
    }

    private static Link instance = null;

    private Link(PositionConfig positionConfig)
    {
        super(positionConfig);

        //        master.setSelectedSensorPosition(kTicksToTop, 0, 100);  // Start able to always reach limit switch
        master.configOpenloopRamp(.15, 100);
        //        TalonFactory.configCurrentLimiting(master, 20, 40, 200);  // Adding with high numbers just in case
        //        master.setControlFramePeriod(ControlFrame.Control_3_General, 10);  // Slow down, wrist responsiveness not critical
        //        master.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 20, 100);  // Slow down, doesn't make decisions off this
        //        // TODO Try configAllowableClosedloopError()
        // TODO Try using Status_10_MotionMagic to improve motion?

        //        stallSensor = new StallSense(master, 25.0, 0.1);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new LinkOpenLoop());
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

