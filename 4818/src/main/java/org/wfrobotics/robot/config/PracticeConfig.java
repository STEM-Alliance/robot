package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.TankConfig;

import java.util.Optional;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem.PositionConfig;
import org.wfrobotics.robot.commands.drive.DriveToTarget;

import edu.wpi.first.wpilibj.command.Command;

/** Practice Robot Config. Override any settings that differ here */
public final class PracticeConfig extends RobotConfig
{
    // Tank
    // _________________________________________________________________________________
    public TankConfig getTankConfig()
    {
        final TankConfig config = new DeepSpaceTankConfig();
        
        config.CLOSED_LOOP_ENABLED = false;

        config.VELOCITY_MAX = 3500.0 / 2;
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.2);
        config.ACCELERATION = config.VELOCITY_PATH ;
        config.STEERING_DRIVE_DISTANCE_P = 0.000022;
        config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        config.OPEN_LOOP_RAMP = 0.05; // how fast do you acellerate
        config.MAX_PERCENT_OUT = 0.85;

        double TURN_SCALING = .35;

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank",
            new MasterConfig[] {
                // Left
                new MasterConfig(10, true, true, new FollowerConfig(12, false), new FollowerConfig(14, false)),
                // Right
                new MasterConfig(11, false, true, new FollowerConfig(13, false), new FollowerConfig(15, false)),
            },
            new Gains[] { 
                new Gains("Velocity", 1, 0.0, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 0),
                new Gains("Turn", 0, 1.0, 0.0000, 0.0 * 4.5, 1023.0 / config.VELOCITY_MAX, 0,
                              (int) (config.VELOCITY_MAX * TURN_SCALING), (int) (config.VELOCITY_MAX * TURN_SCALING)),
            }
        );

        config.GEAR_RATIO_LOW = (54.0 / 32.0);
        config.SCRUB = 0.98;
        config.WHEEL_DIAMETER = 6  + 3.0 / 8.0;
        config.WIDTH = 27.0;

        return config;
    }

    public class DeepSpaceTankConfig extends TankConfig 
    {
        @Override
        public Command getTeleopCommand()
        {
            return new DriveToTarget();
        }
    }

    // Climb
    // _________________________________________________________________________________
    public PnuaticConfig getPnumaticConfig()
    {
        final PnuaticConfig config = new PnuaticConfig();
                 
        // Hardware
        config.kAddressPCMGrippers =5;
        config.kAddressPCMShifter =0;
        config.kAddressPCMPoppers = 0;
        config.kAddressPCMLockers = 1;
        config.kAddressPCMPushUp =2;
        config.kAddressPCMDeployer=3;
        config.kAddressPCMMystory=4;
    
        // intake
        config.kAddressSolenoidPoppersF =7;
        config.kAddressSolenoidPoppersB=6;
        //climb -> Hug
        config.kAddressSolenoidGrippersF=4;
        config.kAddressSolenoidGrippersB=5;
        //elevator -> Shift
        config.kAddressSolenoidShifterF=3;
        config.kAddressSolenoidShifterB=2;
        // climb -> lock
        config.kAddressSolenoidLockersF =0;
        config.kAddressSolenoidLockersB=1;
        // climb -> Push Bumpers Above
        config.KAddressSolenoidPushUpF=2;
        config.KAddressSolenoidPushUpB=3;
        // climb -> shove the mech down
        config.KAddressSolenoidDeployerF=0;
        config.KAddressSolenoidDeployerB=1;
        // unknown
        config.KAddressSolenoidMystoryF=3;
        config.KAddressSolenoidMystoryB=4;

        return config;
    }
    // Elevator
    // _________________________________________________________________________________

    // Hardware
    public PositionConfig getElevatorConfig()
    {
        int kTicksToTop = 143000;  // Good as of March 20th
        double kVelocityMax = 8500.0;  // Good as of March 21st
        int kCruise = (int) (kVelocityMax * 0.975);
        int kAcceleration = (int) (kCruise * 3.50);

        final PositionConfig c = new PositionConfig();

        c.kClosedLoop = new ClosedLoopConfig("Lift",
            new MasterConfig[] {
                new MasterConfig(17, false, true, new FollowerConfig(16, true, false)),
            },
            new Gains[] {
                new Gains("Motion Magic", 0, 0.55, 0.0001, 0.6, 1023.0 / kVelocityMax, 20, kCruise, kAcceleration),
            }
        );
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 56.0;  // Good as of March 21st
        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        c.kSoftwareLimitB = Optional.of(-100);
        c.kFeedForward = Optional.of(0.11);
        // c.kTuning = Optional.of(true);

        return c;
    }
    
    public final double kElevatorHeightCloseEnoughDegrees = 2.0;

    // Intake
    // _________________________________________________________________________________

    // Hardware
    public final double kIntakeDistanceTimeout = 0.025; // time in secounds 
    public final int kAddressTalonCargo = 8;
    public final boolean kInvertTalonCargo = true;

    // Link
    // _________________________________________________________________________________
    public PositionConfig getLinkConfig() 
    {
        final PositionConfig c = new PositionConfig();

        int kTicksToTop = 10950;  // Good as of March 21st
        int kVelocityMax = 2000;  // Good as of March 21st
        int kVelocityCruise = (int) (kVelocityMax * 0.95);
        int kAcceleration = (int) (kVelocityCruise * 4.0);

        c.kClosedLoop = new ClosedLoopConfig("Link",
            new MasterConfig[] {
                new MasterConfig(9, true, false),
            },
            new Gains[] {
                new Gains("Motion Magic", 0, 0.25, 0.0001, 1.0, 1023.0 / kVelocityMax, 40, kVelocityCruise, kAcceleration),
            }
        );
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 90.0;
        c.kSoftwareLimitT = Optional.of(c.kTicksToTop);
        c.kFeedForward = Optional.of(-0.075);  // Good as of March 21st
        // c.kTuning = Optional.of(true);
        return c;
    }

    public final double kLinkCloseEnoughDegrees = 2.0;

    // SuperStructure
    // _________________________________________________________________________________
    public final int kAddressInfraredL = 1;
    public final int kAddressInfraredR = 2;

    // Vision
    // _________________________________________________________________________________
    public final double kVisionP = 0.055;  // March 23rd, linear PID
    public final double kVisionI = 0.0002;  // March 23rd, linear PID
    public final double kVisionD = 0.0;  // March 23rd, linear PID

    // Constructor
    // _________________________________________________________________________________
    protected PracticeConfig()
    {
        cameraStream = Optional.of(false);
    }
}
