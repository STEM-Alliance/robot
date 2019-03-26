package org.wfrobotics.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.climb.ClimbNone;
import org.wfrobotics.robot.config.RobotConfig;

/** @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead */
public final class Climb extends PositionBasedSubsystem
{
    public static Climb getInstance()
    {
        if (instance == null)
        {
            instance = new Climb(RobotConfig.getInstance().getClimbConfig());
        }
        return instance;
    }
    private static Climb instance = null;


    TalonSRX pullMaster;
    TalonSRX pullSlave;

    private Climb(PositionConfig positionConfig)
    {
        super(positionConfig);
        pullMaster = TalonFactory.makeTalon(31);
        pullSlave = TalonFactory.makeFollowerTalon(32, pullMaster);
        pullSlave.setInverted(false);
        pullMaster.setInverted(true);

        master.setControlFramePeriod(ControlFrame.Control_3_General, 10);  // Slow down, responsiveness not critical
    }

    public void setPullers(Double speed)
    {
        pullMaster.set(ControlMode.PercentOutput, speed);
    }
    protected void initDefaultCommand()
    {
        setDefaultCommand(new ClimbNone());
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

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();
        ClosedLoopConfig config = RobotConfig.getInstance().getClimbConfig().kClosedLoop;

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkClosedLoopConfig(config));
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));
        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}