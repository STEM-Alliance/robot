package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.swerve.AutoDrive;
import org.wfrobotics.reuse.commands.drive.swerve.AutoDriveCoast;
import org.wfrobotics.reuse.commands.drive.swerve.AutoDriveWithHeading;
import org.wfrobotics.robot.commands.Lift;
import org.wfrobotics.robot.config.Autonomous.POSITION_ROTARY;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

// TODO figure out why we need config and/or easier way

/**
 * Autonomously score the gear
 * This command group attempts to position for and score a gear during the first 15 seconds of the game.
 */
public class AutoGear extends CommandGroup
{
    public static class Config
    {
        public final double timeApproachAirship, angleSpring, approachSpringX, shootAngle;

        private Config(double timeApproachAirship, double approachSpringAngle, double approachSpringX, double shootAngle)
        {
            this.timeApproachAirship = timeApproachAirship;
            angleSpring = approachSpringAngle;
            this.approachSpringX = approachSpringX;
            this.shootAngle = shootAngle;
        }

        public static Config getConfig(POSITION_ROTARY startingPosition)
        {
            // Assume values are Red Alliance (boiler on your right)
            if (startingPosition == POSITION_ROTARY.CENTER)
            {
                return new Config(1.65, 0, 0, 0);
            }
            else if(DriverStation.getInstance().getAlliance() == Alliance.Red)
            {
                if(startingPosition == POSITION_ROTARY.SIDE_BOILER)
                {
                    return new Config(5, VisionGear.HEXAGON_ANGLE, -.4, -VisionGear.HEXAGON_ANGLE);
                }
                else if(startingPosition == POSITION_ROTARY.SIDE_LOADING_STATION)
                {
                    return new Config(5, 180-VisionGear.HEXAGON_ANGLE, .4, VisionGear.HEXAGON_ANGLE);
                }
                else
                {
                    return new Config(0, 0, 0, 0);
                }
            }
            else
            {
                if(startingPosition == POSITION_ROTARY.SIDE_BOILER)
                {
                    return new Config(5, 180 - VisionGear.HEXAGON_ANGLE, .4, -VisionGear.HEXAGON_ANGLE);
                }
                else if(startingPosition == POSITION_ROTARY.SIDE_LOADING_STATION)
                {
                    return new Config(5, VisionGear.HEXAGON_ANGLE, -.4, VisionGear.HEXAGON_ANGLE);
                }
                else
                {
                    return new Config(0, 0, 0, 0);
                }
            }
        }
    }

    public enum MODE {DEAD_RECKONING, VISION};

    private int signX;
    private final Config config;

    public  AutoGear(POSITION_ROTARY startPosition, MODE mode)
    {
        signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1:-1;  // X driving based on alliance for mirrored field
        config = Config.getConfig(startPosition);

        // Drive to spring
        addParallel(new Lift());  // Make sure we're still lifting
        addSequential(new AutoDriveWithHeading(0, .8, 90, 1));

        // Get to spring
        if(startPosition == POSITION_ROTARY.SIDE_BOILER || startPosition == POSITION_ROTARY.SIDE_LOADING_STATION)
        {
            addSequential(new AutoDrive(0, .8, .35));
            addSequential(new AutoDriveWithHeading(0, 0, config.angleSpring, 1.25));
        }
        addSequential(new AlignWithSpring());

        // Drive closer to spring
        if(startPosition == POSITION_ROTARY.SIDE_BOILER || startPosition == POSITION_ROTARY.SIDE_LOADING_STATION)  // Drive in front of the spring
        {
            addSequential(new AutoDriveCoast(0, .3, 0.1));  // Wheels forward, we messed it up pivoting
            addSequential(new AutoDriveWithHeading(config.approachSpringX,  Math.abs(config.approachSpringX), config.angleSpring, .75));
        }

        // Drive into spring
        if (mode == MODE.DEAD_RECKONING)
        {
            if(startPosition == POSITION_ROTARY.CENTER)
            {
                addSequential(new AutoDriveCoast(0, .5, config.timeApproachAirship));
            }
            else
            {
                addSequential(new AutoDriveWithHeading(signX * config.approachSpringX, Math.abs(config.approachSpringX), config.angleSpring, 1));
            }
        }
        else if (mode == MODE.VISION)
        {
            addSequential(new VisionGearDropAndBackup());
        }
    }
}
