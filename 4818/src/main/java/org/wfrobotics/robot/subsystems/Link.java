package org.wfrobotics.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.link.LinkOpenLoop;
import org.wfrobotics.robot.commands.link.LinkZeroThenOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The wrist consists of a BAG motor to rotate the intake
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class Link extends PositionBasedSubsystem
{
    private static final double kFeedForwardHasLink = 0;  // Practice bot

    public static Link getInstance()
    {
        if (instance == null)
        {
            instance = new Link(RobotConfig.getInstance().getLinkConfig());
        }
        return instance;
    }
    
    public void reportState()
    {
        super.reportState();
        SmartDashboard.putString("Link Command", getCurrentCommandName());
    }

    private static Link instance = null;
    private Link(PositionConfig positionConfig)
    {
        super(positionConfig);

        //        master.setSelectedSensorPosition(kTicksToTop, 0, 100);  // Start able to always reach limit switch
        master.configOpenloopRamp(0.5, 100);
        
        //        TalonFactory.configCurrentLimiting(master, 20, 40, 200);  // Adding with high numbers just in case
        //        master.setControlFramePeriod(ControlFrame.Control_3_General, 10);  // Slow down, wrist responsiveness not critical
        //        master.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 20, 100);  // Slow down, doesn't make decisions off this
        //        // TODO Try configAllowableClosedloopError()
        // TODO Try using Status_10_MotionMagic to improve motion?

        //        stallSensor = new StallSense(master, 25.0, 0.1);

        TalonFactory.configCurrentLimiting(master, 15, 25, 30);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new LinkZeroThenOpenLoop());
        // setDefaultCommand(new LinkZeroThenOpenLoop());
    }
    @Override
    protected void setMotor(ControlMode mode, double val)
    {
        final boolean hasGamePiece = true;  // TODO get from intake
        final double antigravity = kFeedForwardHasLink;
        final double feedforward = (getPosition() < kFullRangeInchesOrDegrees || mode == ControlMode.MotionMagic) ? antigravity : 0.0;

        SmartDashboard.putNumber("Lift FeedForward", feedforward);
        master.set(mode, val, DemandType.ArbitraryFeedForward, feedforward);
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

