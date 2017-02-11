package org.wfrobotics.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoShoot extends CommandGroup 
{
    public enum MODE {DIRECT, HOPPER};
    enum STARTPOS {LEFT, CENTER, RIGHT};

    
    public AutoShoot(STARTPOS startPos)
    {
        // Just like AutoGear, think through this by writing pseudocode, then implement
        //check what place you start in
        //Programmed for RED team for now
        if(startPos == STARTPOS.CENTER)
        {
            //TODO Fill in
            // Drive past the Base line
            // drive back to the hopper and hit it
            // drive to correct shooting distance
            addSequential (new VisionShoot());

        }
        else if (startPos == STARTPOS.LEFT)
        {
            //TODO Fill in
            // Drive past the Base line
            // drive back to the hopper and hit it
            // drive to correct shooting distance
            addSequential (new AutoDrive(0, 1, 0, 2)); //not correct values
            addSequential (new VisionShoot()); 

        }
        else if (startPos == STARTPOS.RIGHT)
        {
            //TODO Fill in
            // Drive past the Base line
            // drive back to the hopper and hit it
            // drive to correct shooting distance
            addSequential (new VisionShoot());
        }
        // TODO Shoot directly at the boiler or do a sequence of commands to load from the hopper and shoot
            // Many steps for the latter

    }
    
    protected void end()
    {
        // TODO definitely some cleanup here
    }
    
    protected void interrupted()
    {
        end();
    }
}
