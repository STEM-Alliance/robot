package org.wfrobotics.robot.paths;

import java.util.ArrayList;

import org.wfrobotics.reuse.config.PathBuilder;
import org.wfrobotics.reuse.config.PathBuilder.Waypoint;
import org.wfrobotics.reuse.math.geometry.Pose2d;
import org.wfrobotics.reuse.math.geometry.Rotation2d;
import org.wfrobotics.reuse.math.geometry.Translation2d;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.reuse.subsystems.control.Path;
import org.wfrobotics.reuse.subsystems.drive.TankMaths;
import org.wfrobotics.robot.config.RobotConfig;

public class SortOfDriveDistance implements PathContainer
{
    public Path buildPath()
    {
        double velocity = TankMaths.ticksToInchesPerSecond(RobotConfig.getInstance().getTankConfig().VELOCITY_MAX);
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0, 0, 0, 0));
        sWaypoints.add(new Waypoint(8.5 * 12, -2 * 12, 2 * 12, velocity));
        sWaypoints.add(new Waypoint(17 * 12, 0 * 12, 0 * 12, velocity));
        sWaypoints.add(new Waypoint(19 * 12, 1 * 12, .5 * 12, velocity));
        sWaypoints.add(new Waypoint(17 * 12, 2 * 12, .5 * 12, velocity));
        sWaypoints.add(new Waypoint(15 * 12, 1 * 12, .5 * 12, velocity));
        sWaypoints.add(new Waypoint(17 * 12, 0 * 12, .5 * 12, velocity));
        sWaypoints.add(new Waypoint(24 * 12, 0 * 12, 0 * 12, velocity));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    public Pose2d getStartPose()
    {
        return new Pose2d(new Translation2d(0, 0), Rotation2d.fromDegrees(0.0));
    }

    public boolean isReversed()
    {
        return false;
    }
}
