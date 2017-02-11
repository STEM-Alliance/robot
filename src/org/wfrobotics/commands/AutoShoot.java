package org.wfrobotics.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoShoot extends CommandGroup 
{
    public enum MODE {PRELOADED, HOPPER};
        public boolean toBaseLine; 
    
    private final MODE mode;
    
    public AutoShoot(MODE mode, boolean toBaseLine)
    {
        this.toBaseLine= toBaseLine;
        this.mode = mode;
        int signx = 1;

        //check what place you start in
        if( DriverStation.getInstance().getAlliance() == Alliance.Red)
        {
            signx = -1;
        }
        if(mode == MODE.HOPPER)
        {
            if(toBaseLine)
            {
                addSequential (new AutoDrive (signx*0, 1, 0, 3)); //pass the Launchpad Line
                addSequential (new AutoDrive(signx*-1, -1, 0, 2)); //comes back towards the hopper
                addSequential (new AutoDrive(signx*-1, 0, 0, 1)); //engage the hopper 
                addSequential (new AutoDrive(signx*.2, -1, 0, 1)); //catch the balls 
                addSequential (new AutoDrive(signx*1, -1, 0, 2)); //get to a more open area
                //TODO correct numbers
                addSequential (new VisionShoot()); 
            }
            else
            {
                addSequential (new AutoDrive(signx*-.2, 1, 0, 3)); //go diagonally to the hopper
                addSequential (new AutoDrive(signx*-1, 0, 0, 1)); //engage the hopper
                addSequential (new AutoDrive(signx*.2, -1, 0, 1)); //catch the balls 
                addSequential (new AutoDrive(signx*1, -1, 0, 2)); //get to a more open area
                //TODO correct numbers
                addSequential (new VisionShoot()); 
               
            }
        }
        else
        {
           addSequential (new AutoDrive (signx*-1, 1, 0, 2));
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
