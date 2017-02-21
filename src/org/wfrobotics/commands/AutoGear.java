package org.wfrobotics.commands;

import org.wfrobotics.commands.Rev.MODE;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Autonomously score the gear
 * This command group attempts to position for and score a gear during the first 15 seconds of the game.
 */
public class AutoGear extends CommandGroup
{
   // public static enum POSITION {LEFT, CENTER, RIGHT};
    public double shootAngle;
    boolean redAlliance;
    int startingPosition = DriverStation.getInstance().getLocation(); //numbered left to right 1-3
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

    public  AutoGear()
    {
        
    Config config = selectConfig(startingPosition);
        
        
        if(DriverStation.getInstance().getAlliance() == Alliance.Red)
        {
            redAlliance = true;
        }

        // Drive forwards while turning towards the airship's spring
        addSequential(new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, config.angleSpring, config.timeForward));
        
        // Drive towards the spring directly ahead
        if(config.timeToSpring > 0)
        {
            addSequential(new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, config.angleSpring, config.timeForward));
        }
        
        // We are at the gear. Score it
        addSequential(new VisionGearDropOff());  // TODO should we put this in a special mode or cancel this if we near the end of autonomous without scoring?
        // If there is any time left, we should shoot or start moving to our next destination (like getting another gear)
        addParallel( new Rev(MODE.SHOOT));
        //TODO TEST NUMBERS
        //move to begin shooting balls in robot
        if(redAlliance)
        {
           
            switch(startingPosition)
            {
                case 1:
                    addSequential( new AutoDrive(0, -Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, 45, 1));
                    addSequential( new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, 0, 0, 2));
                    break;
                case 2:
                    addSequential( new AutoDrive(0, -Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, 90, 1));
                    addSequential( new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, 0, 0, 1));
                    break;
                case 3:
                    addSequential( new AutoDrive(0, -Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, 135, 1));
                    addSequential( new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, 0, 0, 1));
                    break;
            }                    
        }
        else
        {
            switch(startingPosition)
            {
                case 1:
                    addSequential( new AutoDrive(0, -Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, 135, 1));
                    addSequential( new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, 0, 0, 1));
                    break;
                case 2:
                    addSequential( new AutoDrive(0, -Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, 90, 1));
                    addSequential( new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, 0, 0, 1));
                    break;
                case 3:
                    addSequential( new AutoDrive(0, -Constants.AUTONOMOUS_DRIVE_SPEED, Constants.AUTONOMOUS_TURN_SPEED, 45, 1));
                    addSequential( new AutoDrive(0, Constants.AUTONOMOUS_DRIVE_SPEED, 0, 0, 2));
                    break;
            }
        }   

        addSequential(new VisionShoot());

    }
    
    private Config selectConfig(int startingPosition)
    {
        if (startingPosition == 2)
        {
            shootAngle = 0;
            return new Config(3, 0, 0);
        }
        else if(startingPosition == 1)
        {
            shootAngle = -45;
            return new Config(5, 2, 45);
        }
        else if(startingPosition == 3)
        {
            shootAngle = 45;
            return new Config(5, 2, -45);
        }
        else
        {
            return new Config(0, 0, 0);
        }
    }
}
