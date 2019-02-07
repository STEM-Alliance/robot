package org.wfrobotics.robot.subsystems;

import java.util.ArrayList;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.ParellelLink.LinkZeroThenOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

/**
 * The wrist consists of a BAG motor to rotate the intake
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class ParellelLink extends PositionBasedSubsystem
{
    public static ParellelLink getInstance()
    {
        if (instance == null)
        {
            instance = new ParellelLink(RobotConfig.getInstance().getLinkConfig());
        }
        return instance;
    }

    private static ParellelLink instance = null;

    //    private TalonSRX master;
    private final ArrayList<BaseMotorController> followers = new ArrayList<BaseMotorController>();

    private ParellelLink(PositionConfig positionConfig)
    {
        super(positionConfig);

        //        master.setSelectedSensorPosition(kTicksToTop, 0, 100);  // Start able to always reach limit switch
        //        master.configOpenloopRamp(.05, 100);
        //        TalonFactory.configCurrentLimiting(master, 20, 40, 200);  // Adding with high numbers just in case
        //        master.setControlFramePeriod(ControlFrame.Control_3_General, 10);  // Slow down, wrist responsiveness not critical
        //        master.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 20, 100);  // Slow down, doesn't make decisions off this
        //        // TODO Try configAllowableClosedloopError()
        // TODO Try using Status_10_MotionMagic to improve motion?

        //        stallSensor = new StallSense(master, 25.0, 0.1);

        //        master = new TalonSRX(10);
        followers.add(TalonFactory.makeFollowers(master, positionConfig.kClosedLoop.masters.get(0)).get(0));
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new LinkZeroThenOpenLoop());
    }

    @Override
    public void cacheSensors(boolean isDisabled)
    {
        //        stalled = stallSensor.isStalled();
        super.cacheSensors(isDisabled);
    }

    //    @Override
    //    public void reportState()
    //    {
    //        super.reportState();
    //        SmartDashboard.putBoolean("Parrel Link Stalled", stalled);
    //        SmartDashboard.putString("Link Running", getCurrentCommand().getName());
    //    }

    /** Current sense limit switch is set. Only the top can trigger this. */
    //    public boolean isStalled()
    //    {
    //        return stalled;
    //    }

    @Override
    //    public void zeroIfAtLimit()
    //    {
    //        if (AtHardwareLimitBottom())
    //        {
    //            zeroEncoder();
    //        }
    //        else if (AtHardwareLimitTop() || isStalled())
    //        {
    //            master.setSelectedSensorPosition(kTicksToTop, 0, 0);
    //            hasZeroed = true;
    //        }
    //    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));

        //        int retries = 10;
        //        while (!hasZeroed() && retries-- > 0)
        //        {
        //            setOpenLoop(-1.0);
        //            Timer.delay(.2);
        //            zeroIfAtLimit();
        //        }
        //        setOpenLoop(0.0);
        //        report.add(AtHardwareLimitBottom(), "Bottom Limit Is Set");
        //        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}

