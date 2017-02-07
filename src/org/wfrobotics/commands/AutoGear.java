package org.wfrobotics.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

// TODO Add this command to the list of commands we can select from while we are setting up the robot per match

/**
 * Autonomously score the gear
 * This command group attempts to position for and score a gear during the first 15 seconds of the game.
 * @author drlindne
 *
 */
public class AutoGear extends CommandGroup
{
    enum STARTPOS {LEFT, CENTER, RIGHT};

    // TODO be smart enough to vary based on which starting position we are at
        // Which means we will have some extra turning and different distances to move
    
    public AutoGear(STARTPOS startPos)
    { 
        //drive to lift        
        //stop at a certain distance before lift
        if(startPos == STARTPOS.CENTER)
        {
        addSequential(new AutoDrive(2, 1, 1, false));
        //Time will most likely vary
        }
        else if (startPos == STARTPOS.LEFT)
        {
            //TODO Fill in
        }
        else if (startPos == STARTPOS.RIGHT)
        {
            //TODO Fill in
        }
        
        //turn towards lift using angle constant
        
        //take picture and retrieve yaw
        
        //shift left/right
        //drive forwards
        //stop for 4 seconds
        // TODO CHEW ON THIS STUDENTS! 
            // This is a great command for you to perfect because you can easily (just deploy changes)
            // and safely test this. Watch out for pesky school walls and other students, they are negative points. 
        
        // What do?
        // TODO Conditionally turn?
        // TODO Move towards airship
        // TODO Conditionally turn?
        // TODO While we have not scored? Maybe we only get one chance?
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
    
    protected void end()
    {
        // TODO?
    }
    
    protected void interrupted()
    {
        // TODO?
    }
}
