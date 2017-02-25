package org.wfrobotics.commands;

import org.wfrobotics.commands.Rev.MODE;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.robot.Robot.POSITION_ROTARY;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

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
            this.angleSpring = approachSpringAngle;
            this.approachSpringX = approachSpringX;
            this.shootAngle = shootAngle;
        }
        
        public static Config getConfig(POSITION_ROTARY startingPosition)
        {
            // Assume values are Red Alliance (boiler on your right)
            if (startingPosition == POSITION_ROTARY.CENTER)
            {
                return new Config(3, 0, 0, 0);
            }
            else if(startingPosition == POSITION_ROTARY.SIDE_BOILER)
            {
                return new Config(5, 45, -.5, -45);
            }
            else if(startingPosition == POSITION_ROTARY.SIDE_LOADING_STATION)
            {
                return new Config(5, -45, .5, 45);
            }
            else
            {
                return new Config(0, 0, 0, 0);
            }
        }            
    }

    private enum POST_GEAR_AUTONOMOUS {GET_GEAR, RECKONING_SHOOT, VISION_SHOOT};

    private final POSITION_ROTARY startPosition;
    private final int signX;
    private final Config config;

    public  AutoGear(POSITION_ROTARY startPosition)
    {
        this.startPosition = startPosition;
        signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1:-1;  // X driving based on alliance for mirrored field
        config = Config.getConfig(startPosition);

        scoreGear();
        postGearAutonomous(POST_GEAR_AUTONOMOUS.GET_GEAR);
    }

    private void scoreGear()
    {
        addSequential(new AutoDrive(signX * 0, .5, signX * .5, config.angleSpring, config.timeApproachAirship));  // Drive forwards, turning towards the airship's spring
        if(startPosition == POSITION_ROTARY.SIDE_BOILER || startPosition == POSITION_ROTARY.SIDE_LOADING_STATION)
        {
            addSequential(new AutoDrive(signX * config.approachSpringX, .5, signX * .5, config.angleSpring, 1));  // Drive towards the spring
        }
        addSequential(new VisionGearDropOff());  // In front of the gear; score it with vision
    }

    private void postGearAutonomous(POST_GEAR_AUTONOMOUS mode)
    {
        addSequential(new AutoDrive(signX * -config.approachSpringX, -.5, signX * .5, config.angleSpring, .5));  // Back out of spring

        if (mode == POST_GEAR_AUTONOMOUS.GET_GEAR)
        {
            addSequential(new AutoDrive(signX * 0, .5, signX * .5, config.angleSpring, .5));  //
        }
        else if (mode == POST_GEAR_AUTONOMOUS.VISION_SHOOT)
        {
            // TODO Get to the boiler reckoning shoot position
            addSequential(new VisionShoot());
        }
        else if (mode == POST_GEAR_AUTONOMOUS.RECKONING_SHOOT)
        {
            // TODO Get to the boiler vision shoot position
            addParallel( new Rev(MODE.SHOOT));
        }
    }
}
