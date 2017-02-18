package org.wfrobotics.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoShoot extends CommandGroup 
{
    public enum MODE {PRELOADED, HOPPER};
    
    public boolean useVision;
    public boolean baselineFirst = true; 
    private final MODE mode;

    public AutoShoot(MODE mode, boolean useVision)
    {
        this.mode = mode;
        int signx = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1:-1; // X driving based on alliance for mirrored field
        
        if (this.mode == MODE.HOPPER && !useVision)
        {
            addSequential (new AutoDrive (0, .75, 0, 4)); // Forward to Baseline Y
            // TODO pause to make transitions easier???
            addSequential (new AutoDrive (0, -.75, 0, 1)); // Backwards to hopper Y
            addSequential (new AutoDrive (1, 180, .1)); // Rotate to face boiler
            addSequential (new AutoDrive (signx * .5, 0, 0, 3)); // Sideways to hopper X
            addSequential (new AutoDrive (signx * .75, 0, 0, .25)); // Sideways to get off wall X
            addSequential (new AutoDrive (0, -.75, 0, .25)); // Backwards to catch position Y
            addSequential (new Shoot(Conveyor.MODE.CONTINUOUS));  // Shoot, no vision
        }
        else if(this.mode == MODE.HOPPER)
        {
            if(baselineFirst)
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
