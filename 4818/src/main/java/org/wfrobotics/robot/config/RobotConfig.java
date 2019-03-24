package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.EnhancedRobotConfig;
import java.util.Optional;

import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.config.TankConfig;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem.PositionConfig;
import org.wfrobotics.robot.commands.drive.DriveToTarget;

import edu.wpi.first.wpilibj.command.Command;

public class RobotConfig extends EnhancedRobotConfig
{
    private static RobotConfig instance = null;

     // Tank
    // _________________________________________________________________________________
    public TankConfig getTankConfig() 
    {
        final TankConfig config = new DeepSpaceTankConfig();

        config.CLOSED_LOOP_ENABLED = false;  // TODO remove after making closed loop an Optional

        config.VELOCITY_MAX = 3500.0 / 2;
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.85);
        config.ACCELERATION = config.VELOCITY_PATH ;
        config.STEERING_DRIVE_DISTANCE_P = 0.000022;  // TODO Make drive distance a Optional and its owm config
        config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        config.OPEN_LOOP_RAMP = 0.05; // how fast do you acellerate
        config.MAX_PERCENT_OUT = 1.0;

        double TURN_SCALING = .35;

        // TODO Make closed loop an Optional

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", 
            new MasterConfig[] {
                // Left
                new MasterConfig(10, true, true, new FollowerConfig(12, false), new FollowerConfig(14, false)),
                // Right
                new MasterConfig(11, false, true, new FollowerConfig(13, false), new FollowerConfig(15, false)),
            },
            new Gains[] {
                new Gains("Velocity", 1, 0.0, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 100),
                new Gains("Turn", 0, 1.0, 0.0000, 0.0 * 4.5, 1023.0 / config.VELOCITY_MAX, 100,
                                (int) (config.VELOCITY_MAX * TURN_SCALING), (int) (config.VELOCITY_MAX * TURN_SCALING)),
            }
        );

        config.GEAR_RATIO_LOW = (54.0 / 32.0);
        config.SCRUB = 0.98;
        config.WHEEL_DIAMETER = 6 + 3.0 / 8.0;
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

    // Pnumatics
    // _________________________________________________________________________________

    public PnuaticConfig getPnumaticConfig()
    {
        final PnuaticConfig config = new PnuaticConfig();
        
        config.kAddressPCMPoppers = 0;
        config.kAddressSolenoidPoppersF = 2; // HATCH
        config.kAddressSolenoidPoppersB = 3; // HATCH

        config.kAddressPCMShifter = 0;
        config.kAddressSolenoidShifterF = 4; // SHIFTER
        config.kAddressSolenoidShifterB = 1; 

        config.kAddressPCMGrippers = 0;
        config.kAddressSolenoidGrippersF = 0;
        config.kAddressSolenoidGrippersB = 5; 
        
        config.kAddressPCMLockers = 0;
        config.kAddressSolenoidLockersF = 6;
        config.kAddressSolenoidLockersB = 7;

        config.kAddressPCMPushUp = 1;
        config.KAddressSolenoidPushUpF = 0;
        config.KAddressSolenoidPushUpB = 1;

        config.kAddressPCMDeployer = 1;
        config.KAddressSolenoidDeployerF = 2;
        config.KAddressSolenoidDeployerB = 3;

        config.kAddressPCMMystory = 1;
        config.KAddressSolenoidMystoryF = 4;
        config.KAddressSolenoidMystoryB = 5;

        return config;
    }

    // Elevator
    // _________________________________________________________________________________

    // Hardware
    public PositionConfig getElevatorConfig()
    {
        int kTicksToTop = 156624;
        double kVelocityMax = 12750.0;
        int kCruise = (int) (kVelocityMax * 0.975);
        int kAcceleration = (int) (kCruise * 3.50);

        final PositionConfig c = new PositionConfig();

        c.kClosedLoop = new ClosedLoopConfig("Lift",
            new MasterConfig[] {
                new MasterConfig(17, false, true, new FollowerConfig(16, true, false))
            },
            new Gains[] {
                new Gains("Motion Magic", 0, 0.55, 0.0001, 0.7, 1023.0 / kVelocityMax, 20,
                                 kCruise, kAcceleration),
            }
        );
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 56.0;  // Good as of March 21st;
        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        c.kSoftwareLimitB = Optional.of(-100);
        c.kFeedForward = Optional.of(0.11);
        // c.kTuning = Optional.of(false);

        return c;
    }
    
    public final double kElevatorOnTargetDegrees = 2.0;

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

        int kTicksToTop = 6500;
        int kVelocityMax = 2100;
        int kVelocityCruise = (int) (kVelocityMax * 0.95);
        int kAcceleration = (int) (kVelocityCruise * 4.0);

        c.kClosedLoop = new ClosedLoopConfig("Link",
            new MasterConfig[] { 
                new MasterConfig(9, true, false)
            },
            new Gains[] {
                new Gains("Motion Magic", 0, 0.25, 0.0001, 1.0, 1023.0 / kVelocityMax, 40,
                                        kVelocityCruise, kAcceleration),
            }
        );
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 100.0;
        // c.kSoftwareLimitT = Optional.of(kTicksToTop);  // TODO don't hit the ground, just pick something a little too big?
        // c.kFeedForward = Optional.of(0.0);  // TODO - add a small one
        c.kTuning = Optional.of(true);

        return c;
    }
    
    public final double kLinkOnTargetDegrees = 3.0;

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
    protected RobotConfig()
    {
        cameraStream = Optional.of(false);
    }

    //  Helper Methods
    // _________________________________________________________________________________

    public static RobotConfig getInstance()
    {
        if (instance == null)
        {
            instance = (RobotConfig) RobotConfigPicker.get(new EnhancedRobotConfig[] {
                // new RobotConfig(),     // Competition robot
                new PracticeConfig(),  // Practice robot difference
            });
        }
        return instance;
    }
}