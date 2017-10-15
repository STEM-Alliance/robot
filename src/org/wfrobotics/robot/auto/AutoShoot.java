package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.swerve.AutoDrive;
import org.wfrobotics.reuse.commands.drive.swerve.AutoDriveCoast;
import org.wfrobotics.reuse.commands.drive.swerve.TurnToHeading;
import org.wfrobotics.robot.commands.Conveyor;
import org.wfrobotics.robot.commands.IntakeSetup;
import org.wfrobotics.robot.commands.Rev;
import org.wfrobotics.robot.commands.Shoot;
import org.wfrobotics.robot.config.Commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoShoot extends CommandGroup
{
    public enum MODE_DRIVE {DEAD_RECKONING_MIDPOINT, DEAD_RECKONING_HOPPER, LIGHT_SENSOR};
    public enum MODE_SHOOT {DEAD_RECKONING, VISION};

    private final double ANGLE_WALL_IMPACT = 175;
    private final double HOPPER_SHOOT_TIME = 15;

    /**
     * Dead reckoning. Shoot from starting position, then drive past the line.
     */
    public AutoShoot()
    {
        int signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1 : -1; // X driving based on alliance for mirrored field

        addParallel(new AutoDriveCoast(0, 0, 0, 5.5)); // GOOD
        addParallel(new IntakeSetup(true)); // GOOD
        addSequential(new Shoot(Conveyor.MODE.CONTINUOUS, Commands.AUGER_SPEED * .8, Commands.AUGER_UNJAM_SPEED, 6)); // GOOD
        addParallel(new Shoot(Conveyor.MODE.OFF)); // GOOD
        addParallel(new Rev(Rev.MODE.FORCE_OFF)); // GOOD
        addParallel(new IntakeSetup(false)); // GOOD
        addSequential(new AutoDriveCoast(signX * .65, 1, 0, ANGLE_WALL_IMPACT, 2.05));  // GOOD
        addSequential(new AutoDrive(signX * 1, 0, 0, ANGLE_WALL_IMPACT, .6));  // GOOD
        //        addParallel(new Rev(Rev.MODE.RAMP)); // Wind-up shooter - NEED TESTING
        //      addParallel(new Rev(Rev.MODE.RAMP)); // Wind-up shooter - NEED TESTING
        addSequential(new AutoDriveCoast(signX * -.5, 0, 0, ANGLE_WALL_IMPACT, .5));  // Get off wall - NEED TESTING
        addParallel(new IntakeSetup(true)); // GOOD
        addParallel(new Shoot(Conveyor.MODE.CONTINUOUS, Commands.AUGER_SPEED * .8, Commands.AUGER_UNJAM_SPEED, HOPPER_SHOOT_TIME)); // GOOD
        addSequential(new AutoDrive(0, 0, 0, -1, HOPPER_SHOOT_TIME)); // Catch balls - GOOD
        addSequential(new AutoDriveCoast(0, 0, 0, -1, 15)); // Excess autonomous for safety - GOOD
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
            addSequential(new AutoDriveCoast(signX * .5, signX * .5, 0, -1, 7.5));  // Drive to midpoint of the line, along the line
        }
        else
        {
            // Trip the hopper
            if (mode == MODE_DRIVE.DEAD_RECKONING_HOPPER )
            {
                addSequential(new AutoDriveCoast(signX * .5, signX * .5, 0, -1, 15));  // Drive to end of the line, along the line
            }
            else if (mode == MODE_DRIVE.LIGHT_SENSOR)
            {
                addSequential(new AutoDriveCoast(signX * .5, signX * .5, 0, -1, 15));  // Drive to end of the line, based on sensor
            }
            addSequential (new AutoDriveCoast (signX*.5, 0, signX * 0, .25));   // Sideways to get off wall X
            addSequential (new AutoDrive(signX * 0, -.5, signX * 0, .5)); // Backwards to the hopper load position Y
            addParallel (new Rev(Rev.MODE.RAMP));                          // Rev while catching balls
            addSequential (new AutoDrive(signX * 0, 0, 0, -1, 3));    // Wait to catch some balls
            // TODO Should we shoot from another position?
            addSequential (new TurnToHeading (180 - signX * 9, .1)); // Rotate to shoot angle, Cero says 9 degrees either side of pointing backwards
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
