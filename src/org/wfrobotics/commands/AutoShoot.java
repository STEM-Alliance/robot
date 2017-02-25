package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.AutoDrive;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoShoot extends CommandGroup 
{
    public AutoShoot()
    {
        int signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1:-1; // X driving based on alliance for mirrored field

        addSequential(new AutoLineDrive(signX * .5, signX * .5, .2, 0, 15));  // Drive to hopper along line
        addSequential (new AutoDrive (signX*.5, 0, signX * 0, 2)); // Sideways to hopper X
        addSequential (new AutoDrive (signX*.5, 0, signX * 0, .25)); // Sideways to get off wall X
        addSequential (new AutoDrive (signX * 0, -.5, signX * 0, .5)); // Backwards to the hopper load position Y
        addSequential (new AutoDrive (signX * 0, 0, signX * 0, 4)); // Wait to catch some balls
        // TODO Should we shoot from another position, such as in the middle of the 45 degree line?
        addSequential (new AutoDrive (signX * .3, 180 + signX * 9, .1)); // Rotate to shoot angle, Cero says 9 degrees either side of pointing backwards
        addSequential (new Shoot(Conveyor.MODE.CONTINUOUS));  // Shoot, no vision YET
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
