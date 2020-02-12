package frc.robot.subsystems;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.config.EnhancedRobotConfig;
import frc.robot.config.RobotContainer;
import frc.robot.config.TankConfig;
import frc.robot.hardware.TalonFactory;
import frc.robot.commands.Driving;
import frc.robot.reuse.hardware.sensors.GyroNavx;
import frc.robot.EnhancedRobot;

// This is the way we brush our teeth, brush our teeth, brush our teeth

public class Drivetrain extends SubsystemBase {

    // Create drive component objects
    private WPI_TalonSRX left1;
    private WPI_TalonSRX left2;
    private WPI_TalonSRX right1;
    private WPI_TalonSRX right2;
    public DifferentialDrive robotDrive;
    private GyroNavx navx;

    
    private final WPI_TalonSRX leftMaster, rightMaster;
    private final ArrayList<BaseMotorController> followers = new ArrayList<BaseMotorController>();
    private final SteeringController steeringDriveDistance;

    private CachedIO cachedIO = new CachedIO();
    

    private double targetDistance, targetHeading, targetDistanceL, targetDistanceR = 0.0;
    private boolean brakeModeEnabled;

    private RobotContainer container;

    private static final boolean kTuning;
    private static final double kDeadbandOpenLoop;
    private static final int kSlotTurnControl;

    static {
        final TankConfig config = EnhancedRobot.getConfig().getTankConfig();
        kDeadbandOpenLoop = config.OPEN_LOOP_DEADBAND;
        kSlotTurnControl = config.CLOSED_LOOP.gainsByName("Turn").kSlot;
        kTuning = config.TUNING;
    }

    public Drivetrain(RobotContainer container) {
        this.container = container;

        // creates gyro
        navx = new GyroNavx();

        // Create motors
        left1 = new WPI_TalonSRX(14);
        left2 = new WPI_TalonSRX(15);
        right1 = new WPI_TalonSRX(10);
        right2 = new WPI_TalonSRX(11);
        
        // Clear any residual bad values
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

        steeringDriveDistance = new SteeringController(config.STEERING_DRIVE_DISTANCE_P, config.STEERING_DRIVE_DISTANCE_I);

        // Set master-slave bindings
        left2.follow(left1);
        right2.follow(right1);

        // Initialize the WPI drivetrain object with our motors
        robotDrive = new DifferentialDrive(left1, right1); // only need to set the masters
        // Set some values for the drivetrain control
        robotDrive.setSafetyEnabled(true); // enable motor safety
        robotDrive.setExpiration(0.1); // timeout for motor safety checks
        robotDrive.setMaxOutput(.8); // default is FULL SEND
        robotDrive.setRightSideInverted(true); // maybe will need to do this
        robotDrive.setDeadband(0.06);

        setDefaultCommand(new Driving(this));
    }
    public double getAngle()
    {
        return navx.getAngle();
    }

    public void driveeeee() {
        robotDrive.arcadeDrive(container.xbox.getRawAxis(1) * -1, container.xbox.getRawAxis(4), true);
        SmartDashboard.putNumber("x", container.xbox.getRawAxis(1));
        SmartDashboard.putNumber("y", container.xbox.getRawAxis(4));

        // robotDrive.tankDrive(Robot.oi.driver.getRawAxis(1)* -1,
        // Robot.oi.driver.getRawAxis(5)* -1, true);
        // System.out.println(Robot.oi.driver.getY());
        // \System.out.println(Robot.oi.driver.getX());
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

    @Override
    public void periodic() {
        // Put code here to be run every loop

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

    public double getGyro()
    {
        return cachedIO.headingDegrees;
    }

    private void setPIDSlot(int slot)
    {
        leftMaster.selectProfileSlot(slot, 0);
        rightMaster.selectProfileSlot(slot, 0);
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
