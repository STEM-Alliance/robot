package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.swerve.AutoDriveWithHeading;
import org.wfrobotics.robot.commands.Conveyor;
import org.wfrobotics.robot.config.Commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoShoot extends CommandGroup
{
    public enum MODE_DRIVE {DEAD_RECKONING_MIDPOINT, DEAD_RECKONING_HOPPER, LIGHT_SENSOR};
    public enum MODE_SHOOT {DEAD_RECKONING, VISION};

    private final double ANGLE_WALL_IMPACT = 175;
    private final double TIME_SHOOT_WHILE_AT_HOPPER = 15;

    /**
     * Dead reckoning. Shoot from starting position, then drive past the line.
     */
    public AutoShoot()
    {
        int signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1 : -1; // X driving based on alliance for mirrored field

        // Shoot from starting position
        addSequential(new AutonomousShoot(Conveyor.MODE.CONTINUOUS, Commands.AUGER_SPEED * .8, Commands.AUGER_UNJAM_SPEED, 6));

        // Drive diagonally towards hopper
        addSequential(new AutoDriveWithHeading(signX * .65, 1, ANGLE_WALL_IMPACT, 2.05));

        // Drive into hopper
        addSequential(new AutoDriveWithHeading(signX * 1, 0, ANGLE_WALL_IMPACT, .6));

        // Get off wall
        addSequential(new AutoDriveWithHeading(signX * -.5, 0, ANGLE_WALL_IMPACT, .5));

        // Catch balls
        addSequential(new AutonomousShoot(Conveyor.MODE.CONTINUOUS, Commands.AUGER_SPEED * .8, Commands.AUGER_UNJAM_SPEED, TIME_SHOOT_WHILE_AT_HOPPER));
    }
}
