package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;


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
    final STARTPOS startPos;
    boolean isMoving = false;
    
    // TODO be smart enough to vary based on which starting position we are at
        // Which means we will have some extra turning and different distances to move
    
    public AutoGear(STARTPOS startpos)
    { 
        startPos = startpos;
        requires(Robot.driveSubsystem);
        requires(Robot.aligningSubsystem);
        //turn towards lift using angle constant
        
        if(startPos == STARTPOS.CENTER)
        {
        addSequential(new AutoDrive(2, 1, 0, 1));
            //Robot.driveSubsystem.driveXY(0, 1, 0);
            isMoving = true;
        //Time will most likely vary
        }
        else if (startPos == STARTPOS.LEFT)
        {
         
            /* Vector vector = new Vector(0, 1);
           Robot.driveSubsystem.driveWithHeading(vector, .5, 45);
           */
           addSequential(new AutoDrive(2, 1, 1, false));
           addSequential(new AutoDrive(45, 0, AutoDrive.MODE.ROTATE));
           isMoving = true;
            //TODO Fill in
        }
        else if (startPos == STARTPOS.RIGHT)
        {
            
            /*
            Vector vector = new Vector(0, 1);
            Robot.driveSubsystem.driveWithHeading(vector, -.5, 315);
            */
            addSequential(new AutoDrive(2, 1, 1, false));
            addSequential(new AutoDrive(315, 0, AutoDrive.MODE.ROTATE));
            isMoving = true;
        }
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
    
    protected void execute()
    {
        if(isMoving == true)
        {
            if(Robot.aligningSubsystem.getData().Yaw > 0) // to far to the right
            {
                Robot.driveSubsystem.driveXY(-0.2, 0, 0);                
            }
            else if(Robot.aligningSubsystem.getData().Yaw < 0)// to far to the left
            {
                Robot.driveSubsystem.driveXY(0.2, 0, 0);
            }
            else if(Robot.aligningSubsystem.getData().Yaw == 0)
            {
                if(Robot.targetingSubsystem.DistanceToTarget() > 0)
                {
                    Robot.driveSubsystem.driveXY(0, 0.5, 0);
                }
                else if(Robot.targetingSubsystem.DistanceToTarget() == 0)
                {
                    Robot.driveSubsystem.driveXY(0, 0, 0);
                    isMoving = false;
                }
            }
        }
    }
    
    protected void end()
    {
        Robot.driveSubsystem.driveXY(0, 0, 0);
    }
    
    protected void interrupted()
    {
        // TODO?
    }

}

