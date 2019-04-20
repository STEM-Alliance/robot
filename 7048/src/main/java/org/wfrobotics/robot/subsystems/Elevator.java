package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.climb.ElevatorOpenLoop;
import org.wfrobotics.robot.config.ArmHeight;
import org.wfrobotics.robot.config.RobotConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;

/**
 * The elevator consists of two independently connected Mini CIM motors to raise/lower the intake and climber
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class Elevator extends PositionBasedSubsystem
{
    public static Elevator getInstance()
    {
        if (instance == null)
        {
            instance = new Elevator(RobotConfig.getInstance().getElevatorConfig());
        }
        return instance;
    }

    private static final double kInchesGroundToZero = ArmHeight.BottomLimit.get();
    private static final int kTickRateBrakeModeObserved = 0;  // TODO Tune
    private static final int kTickRateSlowEnough = kTickRateBrakeModeObserved + 200;  // TODO Tune

    double rampSpeed = 1;

    private static Elevator instance = null;

    private Elevator(PositionConfig positionConfig)
    {
        super(positionConfig);

        //master.setSelectedSensorPosition(0, 0, 100);
        //TalonFactory.configCurrentLimiting(master, 15, 30, 200);  // TODO Tune
        //master.configClosedloopRamp(0.15, 100);  // Soften reaching setpoint TODO Tune
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new ElevatorOpenLoop());
    }

    /** Inches off ground */
    @Override
    public double getPosition()
    {
        return NativeToPosition(getPositionNative()) + kInchesGroundToZero;
    }

    /** Use to improve isFinished() criteria for closed loop commands? */
    public boolean onTarget()
    {
        return Math.abs(getVelocityNative()) < kTickRateSlowEnough;
    }

    @Override
    public void setOpenLoop(double percent)
    {
        // double posMax = 21800;
        // rampSpeed=1;
        // SmartDashboard.putString("liftRamping?", "None");
        // if(getPositionNative()>posMax-5000&&percent>0.1){
        //     SmartDashboard.putString("liftRamping?", "Top");
        //     rampSpeed = (posMax-getPositionNative())/5000.0;
        //     if(rampSpeed*percent<0.1)
        //         rampSpeed = 0.1/percent;
        // }
        // if(getPositionNative()<3000&&percent<-0.1){
        //     SmartDashboard.putString("liftRamping?", "Bottom");
        //     rampSpeed = (getPositionNative()+2000)/5000.0;
        //     if(rampSpeed*percent<0.1)
        //         rampSpeed = 0.1/percent;
        // }


        // if(getPositionNative()>22000&&percent>0)
        // {
        //     rampSpeed=((24250-getPositionNative())/4250);
        //     if(rampSpeed<=.1){
        //         rampSpeed=.1;
        //     }
        // }

        // if(getPositionNative()<4250&&percent<0)
        // {
        //     rampSpeed=((getPositionNative()+1000)/4250);
        //     if(rampSpeed<=.1){
        //         rampSpeed=.1;
        //     }
        // }

        final double speed = percent*rampSpeed;
        SmartDashboard.putNumber("liftSpeed", speed);
        SmartDashboard.putNumber("liftRamping", rampSpeed);
        setMotor(ControlMode.PercentOutput, speed);
    }

    @Override
    public void setClosedLoop(double inchesOffGround)
    {
        final double inchesFromZero = inchesOffGround - kInchesGroundToZero;

        setMotor(ControlMode.MotionMagic, PositionToNative(inchesFromZero));  // Stalls motors
    }

    @Override
    protected void setMotor(ControlMode mode, double val)
    {
        final double feedforward = 0.0;

        master.set(mode, val, DemandType.ArbitraryFeedForward, feedforward);
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkFirmware(followers));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));
        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}