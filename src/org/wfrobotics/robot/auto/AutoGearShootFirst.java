package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.swerve.AutoDrive;
import org.wfrobotics.reuse.commands.drive.swerve.AutoDriveCoast;
import org.wfrobotics.robot.commands.Conveyor;
import org.wfrobotics.robot.commands.Lift;
import org.wfrobotics.robot.config.Autonomous.POSITION_ROTARY;
import org.wfrobotics.robot.config.Commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Autonomously score the gear
 * This command group attempts to position for and score a gear during the first 15 seconds of the game.
 */
public class AutoGearShootFirst extends CommandGroup
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

    private final Config config;

    public  AutoGearShootFirst(POSITION_ROTARY startPosition)
    {
        config = Config.getConfig(startPosition);

        // Enable the lift, intake, and shoot. Assume not moving prior
        addParallel(new Lift(Lift.MODE.UP));
        addSequential(new AutonomousShoot(Conveyor.MODE.CONTINUOUS, Commands.AUGER_SPEED * .8, Commands.AUGER_UNJAM_SPEED, 5));

        // Drive to spring
        addParallel(new Lift());  // Make sure we're still lifting
        addSequential(new AutoDrive(0, .7, 0, -1, 1.75));  // keep our heading for a bit first to not ram the wall

        // Get to spring
        if(startPosition == POSITION_ROTARY.SIDE_BOILER || startPosition == POSITION_ROTARY.SIDE_LOADING_STATION)
        {
            addSequential(new AutoDrive(0, .8, 0, config.angleSpring, .25));
            addSequential(new AutoDrive(0, 0, 0, config.angleSpring, 1.25));
        }
        addSequential(new AlignWithSpring());

        // Drive closer to spring
        if(startPosition == POSITION_ROTARY.SIDE_BOILER || startPosition == POSITION_ROTARY.SIDE_LOADING_STATION)
        {
            addSequential(new AutoDriveCoast(0, .3, 0, -1, 0.1));  // Wheels forward, we messed it up pivoting
            addSequential(new AutoDrive(config.approachSpringX,  Math.abs(config.approachSpringX), 0, config.angleSpring, .75));
        }

        // Drive into spring
        addSequential(new VisionGearDropAndBackup());
    }
}
