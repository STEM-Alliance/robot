package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.elevator.ElevatorZeroThenOpenLoop;
import org.wfrobotics.robot.config.PnuaticConfig;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.StatusFrame;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The elevator consists of two independently connected Mini CIM motors to raise/lower the intake and climber
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public final class Elevator extends PositionBasedSubsystem
{
    public static Elevator getInstance()
    {
        if (instance == null)
        {
            instance = new Elevator(RobotConfig.getInstance().getElevatorConfig());
        }
        return instance;
    }

    private static final int kTickRateBrakeModeObserved = 400;  // Observed on practice bot March 20th
    private static final int kTickRateSlowEnough = kTickRateBrakeModeObserved + 200;

    private static Elevator instance = null;
    private final DoubleSolenoid shifter;

    private Elevator(PositionConfig positionConfig)
    {
        super(positionConfig);
        final PnuaticConfig pConfig = RobotConfig.getInstance().getPnumaticConfig();

        //        master.configClosedloopRamp(0.15, 100);  // Soften reaching setpoint TODO Tune
        master.configOpenloopRamp(.15, 100);  // TODO Tune because we switched to miniCIMs
        master.setSelectedSensorPosition((int) (positionConfig.kTicksToTop * 2.0), 0, 100);  // Always be able to zero
        master.setStatusFramePeriod(StatusFrame.Status_1_General, 10, 100);  // Faster for limit switch
        TalonFactory.configCurrentLimiting(master, 25, 30, 20);

        shifter = new DoubleSolenoid(pConfig.kAddressPCMShifter, pConfig.kAddressSolenoidShifterF, pConfig.kAddressSolenoidShifterB);

        setShifter(false);
    }

    public void initDefaultCommand()
    {
        // setDefaultCommand(new ElevatorOpenLoop());
        setDefaultCommand(new ElevatorZeroThenOpenLoop());  // TODO works but need sensor fixed on practice bot first
    }

    /** Velocity slow enough. Use to improve isFinished() criteria for closed loop commands */
    public boolean onTarget()
    {
        return Math.abs(getVelocityNative()) < kTickRateSlowEnough;
    }
    public boolean getLiftNotClimb( )
    {
        return liftNotClimb;
    }
    private boolean liftNotClimb = true;

    @Override
    public void zeroIfAtLimit()
    {
        if(AtHardwareLimitBottom() && liftNotClimb)
        {
            zeroEncoder();
        }
    }
    public void reportState()
    {
                SmartDashboard.putString("Elevator Command", getCurrentCommandName());
    }
    public void setShifter(boolean liftNotClimb)
    {
        Value desired = (liftNotClimb) ? Value.kForward : Value.kReverse;
        this.liftNotClimb = !liftNotClimb;

        shifter.set(desired);
        configureClimbMode();

    }
    public void configureClimbMode()
    {
        // DRL - This was causing bottom limit switch not to stop motor
        if (!liftNotClimb)
        {
            master.overrideLimitSwitchesEnable(true);
            // master.configForwardSoftLimitThreshold(79000);
            // master.configReverseSoftLimitThreshold(-15867);
        }
        else
        {
        	master.overrideLimitSwitchesEnable(false);
        }
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();
        ClosedLoopConfig config = RobotConfig.getInstance().getElevatorConfig().kClosedLoop;

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkClosedLoopConfig(config));
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkFirmware(followers));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));
        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}
