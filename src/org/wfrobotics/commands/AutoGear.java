package org.wfrobotics.commands;

import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Autonomously score the gear
 * This command group attempts to position for and score a gear during the first 15 seconds of the game.
 */
public class AutoGear extends CommandGroup
{
    public static enum POSITION {LEFT, CENTER, RIGHT}
    public class Config
    {
        public final double timeForward, timeToSpring, angleSpring;
        
        public Config(double timeForward, double timeToSpring, double angleSpring)
        {
            this.timeForward = timeForward;
            this.timeToSpring = timeToSpring;
            this.angleSpring = angleSpring;
        }
    }    

    public AutoGear(POSITION startingPosition)
    {
        Config config = selectConfig(startingPosition);
        
        // Drive forwards while turning towards the airship's spring
        addSequential(new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, config.angleSpring, config.timeForward));
        
        // Drive towards the spring directly ahead
        if(config.timeToSpring > 0)
        {
            addSequential(new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, config.angleSpring, config.timeForward));
        }
        
        // We are at the gear. Score it
        addSequential(new VisionGearDropOff());  // TODO should we put this in a special mode or cancel this if we near the end of autonomous without scoring?
        addParallel(new LED(Led.HARDWARE.ALL, LED.MODE.BLINK, 5));
        // If there is any time left, we should shoot or start moving to our next destination (like getting another gear   // TODO While we have not scored? Maybe we only get one chance?
        // Perhaps the body of this is a function that adds commands or it's own private Class CommandGroup
        // TODO Move towards spring
        // TODO Ultrasonic black magic to do this? Not only sense distance but that we are square?
        // TODO Or Camera? There's those reflective tape strips centered on the spring.
        // TODO Detect spring?
        // TODO If we make it, double check this command will alert the human player with LEDs
        // TODO Then we are going to want to leave this loop
        // TODO reset???
        // TODO Back out
        // TODO If we have scored, this condones serious flaunting (of our LEDs)?
        // TODO Turn towards the boiler?
        // TODO if we are going to shoot, revving while turning would save time
        // TODO Should we try to shoot? Should this be optional? Should this be smart?
    }
    
    private Config selectConfig(POSITION startingPosition)
    {
        if (startingPosition == POSITION.CENTER)
        {
            return new Config(3, 0, 0);
        }
        else if(startingPosition == POSITION.LEFT)
        {
            return new Config(5, 2, 45);
        }
        else if(startingPosition == POSITION.RIGHT)
        {
            return new Config(5, 2, -45);
        }
        else
        {
            return new Config(0, 0, 0); 
        }
    }
}
