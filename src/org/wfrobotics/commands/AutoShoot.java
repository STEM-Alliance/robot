package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.AutoDrive;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoShoot extends CommandGroup 
{
    public enum MODE_DRIVE {DEAD_RECKONING_MIDPOINT, DEAD_RECKONING_HOPPER, LIGHT_SENSOR};
    public enum MODE_SHOOT {DEAD_RECKONING, VISION};
    
    /**
     * Dead reckoning. Shoot from starting position, then drive past the line.
     */
    public AutoShoot()
    {
        int signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1 : -1; // X driving based on alliance for mirrored field
        
        addParallel(new AutoDrive(0, 0, 0, 6));
        addParallel(new IntakeSetup(true));
        addSequential(new Shoot(Conveyor.MODE.CONTINUOUS, Constants.AUGER_SPEED * .8, Constants.AUGER_UNJAM_SPEED, 6));
        addParallel(new Shoot(Conveyor.MODE.OFF));
        addParallel(new Rev(Rev.MODE.FORCE_OFF));
        addParallel(new IntakeSetup(false));
        addSequential(new AutoDrive(signX * .65, 1, 0, 180, 4.3));
        addSequential(new AutoDrive(0, -1, 0, -1, .75));
        addParallel(new Shoot(Conveyor.MODE.CONTINUOUS, Constants.AUGER_SPEED * .8, Constants.AUGER_UNJAM_SPEED, 9));
        addSequential(new AutoDrive(0, 0, 0, -1, 9));
    }

    public AutoShoot(MODE_DRIVE drive, MODE_SHOOT shoot)
    {
        do_drive(drive);
        do_shoot(shoot);
    }
    
    private void do_drive(MODE_DRIVE mode)
    {
        int signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1:-1; // X driving based on alliance for mirrored field
        
        // Move to where you want to shoot
        if (mode == MODE_DRIVE.DEAD_RECKONING_MIDPOINT)
        {
            addSequential(new AutoDrive(signX * .5, signX * .5, 0, -1, 7.5));  // Drive to midpoint of the line, along the line
        }
        else 
        {
            // Trip the hopper
            if (mode == MODE_DRIVE.DEAD_RECKONING_HOPPER )
            {
                addSequential(new AutoDrive(signX * .5, signX * .5, 0, -1, 15));  // Drive to end of the line, along the line
            }
            else if (mode == MODE_DRIVE.LIGHT_SENSOR)
            {
                addSequential(new AutoLineDrive(signX * .5, signX * .5, 0, -1, 15));  // Drive to end of the line, based on sensor
            }
            addSequential (new AutoDrive (signX*.5, 0, signX * 0, .25));   // Sideways to get off wall X
            addSequential (new AutoDrive (signX * 0, -.5, signX * 0, .5)); // Backwards to the hopper load position Y
            addParallel (new Rev(Rev.MODE.RAMP));                          // Rev while catching balls
            addSequential (new AutoDrive (signX * 0, 0, 0, -1, 3));    // Wait to catch some balls
            // TODO Should we shoot from another position?
            addSequential (new AutoDrive (signX * .3, 180 - signX * 9, .1)); // Rotate to shoot angle, Cero says 9 degrees either side of pointing backwards
        }
    }
    
    private void do_shoot(MODE_SHOOT mode)
    {
        if (mode == MODE_SHOOT.DEAD_RECKONING)
        {
            addSequential(new Shoot(Conveyor.MODE.CONTINUOUS));  
        }
        else if (mode == MODE_SHOOT.VISION)
        {
            addSequential(new VisionShoot());
        }
    }
}
