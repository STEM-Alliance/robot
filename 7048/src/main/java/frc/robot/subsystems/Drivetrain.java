package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.config.TankConfig;
import frc.robot.hardware.Gyro;
import frc.robot.hardware.TalonFactory;
import frc.robot.commands.Driving;
import frc.robot.subsystems.PathFollower.Parameters;
import frc.robot.EnhancedRobot;
import frc.robot.RobotStateBase;

public class Drivetrain extends SubsystemBase {

    // Create drive component objects
    public DifferentialDrive robotDrive;
    
    private TANK_MODE controlMode = TANK_MODE.OPEN_LOOP;
    private final WPI_TalonSRX leftMaster, rightMaster;
    private final ArrayList<BaseMotorController> followers = new ArrayList<BaseMotorController>();
    private final Gyro gyro;
    private final SteeringController steeringDriveDistance;

    private CachedIO cachedIO = new CachedIO();
    
    private final RobotStateBase state = EnhancedRobot.getState();
    private double targetDistance, targetHeading, targetDistanceL, targetDistanceR = 0.0;
    private boolean brakeModeEnabled;

    private static final Parameters kPathConfig;
    private static final boolean kTuning;
    private static final double kDeadbandOpenLoop;
    private static final int kSlotTurnControl;

    static {
        final TankConfig config = EnhancedRobot.getConfig().getTankConfig();
        kDeadbandOpenLoop = config.OPEN_LOOP_DEADBAND;
        kSlotTurnControl = config.CLOSED_LOOP.gainsByName("Turn").kSlot;
        kTuning = config.TUNING;
        kPathConfig = config.getPathConfig();
    }

    public Drivetrain() {
        final TankConfig config = EnhancedRobot.getConfig().getTankConfig();
        final List<WPI_TalonSRX> masters = TalonFactory.makeClosedLoopTalon(config.CLOSED_LOOP);

        for (WPI_TalonSRX master : masters)
        {
            master.setControlFramePeriod(ControlFrame.Control_3_General, 5);
            master.configOpenloopRamp(config.OPEN_LOOP_RAMP, 100);
            master.configNeutralDeadband(kDeadbandOpenLoop, 100);
            master.configPeakOutputForward(config.MAX_PERCENT_OUT);
            master.configPeakOutputReverse(-config.MAX_PERCENT_OUT);

            if (config.CLOSED_LOOP_ENABLED)
            {
                master.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, 100);
                master.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_50Ms, 100);
                master.configVelocityMeasurementWindow(1, 100);
                TalonFactory.configFastErrorReporting(master, kTuning);
            }
            else
            {
                TalonFactory.configOpenLoopOnly(master);
            }
        }
        leftMaster = masters.get(0);
        rightMaster = masters.get(1);

        for (BaseMotorController follower : TalonFactory.makeFollowers(leftMaster, config.CLOSED_LOOP.masters.get(0)))
        {
            followers.add(follower);
        }
        for (BaseMotorController follower : TalonFactory.makeFollowers(rightMaster, config.CLOSED_LOOP.masters.get(1)))
        {
            followers.add(follower);
        }

        gyro = config.getGyroHardware();
        steeringDriveDistance = new SteeringController(config.STEERING_DRIVE_DISTANCE_P, config.STEERING_DRIVE_DISTANCE_I);
        if (config.CLOSED_LOOP_ENABLED)
        {
            // BackgroundUpdater.getInstance().register(updater);
        }

        zeroEncoders();
        brakeModeEnabled = true;  // Opposite to force initial setBrake() to succeed
        setBrake(false);
        setGyro(0.0);

        setDefaultCommand(new Driving(this));
    }

	public void drive() {
	}
 
    public synchronized void driveDistance(double inchesForward)
    {
        setPIDSlot(kSlotTurnControl);
        targetDistance = inchesForward;
        targetHeading = getGyro();
        steeringDriveDistance.reset(Timer.getFPGATimestamp());
        targetDistanceL = targetDistance + getDistanceInchesL();
        targetDistanceR = targetDistance + getDistanceInchesR();
        state.resetDistanceDriven();
        controlMode = TANK_MODE.DISTANCE;
        updateDriveDistance();  // Extra cycle
    }

	public synchronized void setBrake(boolean enable)
    {
        if (brakeModeEnabled != enable)
        {
            final NeutralMode mode = (enable) ? NeutralMode.Brake : NeutralMode.Coast;
            leftMaster.setNeutralMode(mode);
            rightMaster.setNeutralMode(mode);
            for (BaseMotorController follower : followers)
            {
                follower.setNeutralMode(mode);
            }
            brakeModeEnabled = enable;
        }
    }

    public synchronized void zeroEncoders()
    {
        leftMaster.setSelectedSensorPosition(0);
        rightMaster.setSelectedSensorPosition(0);
        cachedIO = new CachedIO();
    }

    public synchronized void setGyro(double angle)
    {
        gyro.zeroYaw(angle);
        cachedIO.headingDegrees = angle;
    }

    public double getGyro()
    {
        return cachedIO.headingDegrees;
    }

    public double getDistanceInchesL()
    {
        return TankMaths.ticksToInches(cachedIO.positionTicksL);
    }

    public double getDistanceInchesR()
    {
        return TankMaths.ticksToInches(cachedIO.positionTicksR);
    }

    private void setPIDSlot(int slot)
    {
        leftMaster.selectProfileSlot(slot, 0);
        rightMaster.selectProfileSlot(slot, 0);
    }

    private void updateDriveDistance()
    {
        final double now = Timer.getFPGATimestamp();
        final boolean doneSteering = Math.abs(state.getDistanceDriven() - targetDistance) < kPathConfig.stop_steering_distance;
        final double adjust = steeringDriveDistance.correctHeading(now, targetHeading, getGyro(), doneSteering);

        leftMaster.set(ControlMode.MotionMagic, targetDistanceL, DemandType.ArbitraryFeedForward, adjust);
        rightMaster.set(ControlMode.MotionMagic, targetDistanceR, DemandType.ArbitraryFeedForward, -adjust);
    }

    private enum TANK_MODE
    {
        OPEN_LOOP,
        LOCK_SETPOINT,
        DISTANCE,
        TURN,
        PATH,
    }

    private class CachedIO
    {
        public int positionTicksL;
        public int positionTicksR;
        public int velocityTicksPer100msL;
        public int velocityTicksPer100msR;
        public double headingDegrees;
        public double pitchDegrees;    
    }
}
