package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.commands.drive.DriveConfig;
import org.wfrobotics.commands.drive.DriveConfig.MODE;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.Robot.POSITION_ROTARY;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
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
                return new Config(2.1, 0, 0, 0);
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

    private enum POST_GEAR_AUTONOMOUS {NONE, GET_GEAR, RECKONING_SHOOT, VISION_SHOOT};
    public enum MODE {DEAD_RECKONING, VISION};

    private final POSITION_ROTARY startPosition;
    private final MODE mode;
    private int signX;
    private final Config config;

    public  AutoGear(POSITION_ROTARY startPosition, MODE mode, boolean shootFirst)
    {
        this.startPosition = startPosition;
        this.mode = mode;
        
        signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1:-1;  // X driving based on alliance for mirrored field
        config = Config.getConfig(startPosition);
        
        if (shootFirst)
        {
            shoot();
        }
        
        driveToSpring(shootFirst);
        
        scoreGear(shootFirst);
    }
    
    private void shoot()
    {
        // enable the lift, intake, and shoot
        addParallel(new Lift(Lift.MODE.UP));
        addParallel(new AutoDrive(0, 0, 0, 5));
        addParallel(new IntakeSetup(true));
        addSequential(new Shoot(Conveyor.MODE.CONTINUOUS, Constants.AUGER_SPEED * .8, Constants.AUGER_UNJAM_SPEED, 5));
        
        // kill all the above
        addParallel(new Shoot(Conveyor.MODE.OFF));
        addParallel(new Rev(Rev.MODE.FORCE_OFF));
        addParallel(new IntakeSetup(false));
        addSequential(new AutoDrive(0, 0, 0, -1, .1));
    }
    
    private void driveToSpring(boolean shootFirst)
    {
        // make sure we're still lifting
        addParallel(new Lift());
        
        if (shootFirst)
        {
            // if we're shooting, we need to keep our heading for a bit first to not ram the wall
            addSequential(new AutoDrive(0, .7, 0, -1, 1.75));
        }
        else
        {
            // otherwise maintain the 90 degree angle
            addSequential(new AutoDrive(0, .8, 0, 90, 1.3));
        }
        
        addSequential(new AutoDrive(0, 0, 0, -1, 0));  // Don't coast GOOD
    }

    private void scoreGear(boolean shootFirst)
    {
        //addSequential(new AutoDrive(signX * 0, .5, signX * .5, config.angleSpring, config.timeApproachAirship));  // Drive forwards, turning towards the airship's spring
        
        //addSequential(new AutoDrive(0,.75,0,-1,config.timeApproachAirship));

        if(startPosition == POSITION_ROTARY.SIDE_BOILER || startPosition == POSITION_ROTARY.SIDE_LOADING_STATION)  // Drive in front of the spring
        {
            if(shootFirst)
            {
                // we have to start pre-spinning to the spring angle
                addSequential(new AutoDrive(0, .8, 0, config.angleSpring, .25));
            }
            else
            {
                addSequential(new AutoDrive(0, .8, 0, -1, .275));
            }
            
            
            // turn to the spring
            addSequential(new AutoDrive(0, 0, 0, -1, 0.1));  // Don't coast GOOD
            addSequential(new AutoDrive(0, 0, 0, config.angleSpring, 1.25));  // Don't coast GOOD
            addSequential(new AutoDrive(0, 0, 0, -1, 0.1));  // Don't coast GOOD

        }
            
            addSequential(new VisionGearStrafe());
            addSequential(new VisionGearPivot());
            addSequential(new AutoDrive(0, 0, 0, -1, 0.1));  // Don't coast GOOD
        if(startPosition == POSITION_ROTARY.SIDE_BOILER || startPosition == POSITION_ROTARY.SIDE_LOADING_STATION)  // Drive in front of the spring
        {
            boolean fieldRelative = Robot.driveSubsystem.getFieldRelative();
            addSequential(new AutoDrive(0, .3, 0, -1, 0.1));  // Wheels forward, we messed it up pivoting
            addSequential(new DriveConfig(DriveConfig.MODE.FIELD_RELATIVE, fieldRelative));
            
            // approach the spring
            addSequential(new AutoDrive(config.approachSpringX,       // Towards the airship
                                        Math.abs(config.approachSpringX),     // Always forwards with X magnitude
                                        0, config.angleSpring, .75));
            addSequential(new AutoDrive(0, 0, 0, -1, 0.1));  // Don't coast GOOD
        }
        
        switch(mode)
        {
            case DEAD_RECKONING:
                if(startPosition == POSITION_ROTARY.CENTER)
                {
                    addSequential(new AutoDrive(0,.5,0,-1,config.timeApproachAirship));
                }
                else
                {
                    addSequential(new AutoDrive(signX * config.approachSpringX, 
                                  Math.abs(config.approachSpringX),
                                  signX * .5, config.angleSpring, 1));
                }
                break;
                
            case VISION:
                // this could potentially be replaced with VisionGearDropAndBackup
                
                addSequential(new VisionGear());  // In front of the gear; score it with vision
                
                // wait a half sec
                addSequential(new AutoDrive(0, 0, 0, -1, .5));
                
                // now drop it and back up
                addParallel(new Lift(Lift.MODE.DOWN));
                if(startPosition == POSITION_ROTARY.CENTER)
                {
                    addSequential(new AutoDrive(0, -.4, 0, -1, 1));
                    addSequential(new AutoDrive(0, 0, 0, -1, 1));  // Don't coast GOOD
                }
                else
                {
                    addSequential(new AutoDrive(-config.approachSpringX,       // away from the airship
                                                -Math.abs(config.approachSpringX),     // Always away with X magnitude
                                                0, config.angleSpring, 1));
                    addSequential(new AutoDrive(0, 0, 0, -1, 1));  // Don't coast GOOD
                }
                break;
            default:
                break;
        }
    }

    private void postGearAutonomous(POST_GEAR_AUTONOMOUS autoType)
    {
        // Back out of spring
        addSequential(new AutoDrive(signX * -config.approachSpringX,  // Away from airship
                      -Math.abs(config.approachSpringX),              // Always backwards with X magnitude
                      signX * .5, config.angleSpring, 1));           

        if (mode == MODE.VISION)
        {
            switch(autoType)
            {
                case GET_GEAR:
                    // TODO Get to the outside of airship (based on starting position) so you can go straight forwards towards the loading station but not over the line
                    addSequential(new AutoDrive(signX * 0, .5, signX * .5, config.angleSpring, .5));
                    break;
                    
                case RECKONING_SHOOT:
                    // TODO Get to the boiler vision shoot position
                    addParallel( new Rev(Rev.MODE.SHOOT));
                    
                case VISION_SHOOT:
                    // TODO Get to the boiler reckoning shoot position
                    addSequential(new VisionShoot());
                    break;
                    
                case NONE:
                default:
                    break;
            }
            if (autoType == POST_GEAR_AUTONOMOUS.VISION_SHOOT)
            {
            }
            else if (autoType == POST_GEAR_AUTONOMOUS.RECKONING_SHOOT)
            {
            }
        }
    }
}
