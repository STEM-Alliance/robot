package org.wfrobotics.robot.paths;

import java.util.ArrayList;

import org.wfrobotics.reuse.config.PathBuilder;
import org.wfrobotics.reuse.config.PathBuilder.Waypoint;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.reuse.math.rigidtransform.RigidTransform2d;
import org.wfrobotics.reuse.math.rigidtransform.Rotation2d;
import org.wfrobotics.reuse.math.rigidtransform.Translation2d;
import org.wfrobotics.reuse.subsystems.control.PathAdaptive;
import org.wfrobotics.reuse.subsystems.drive.TankMaths;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

public class SortOfDriveDistance implements PathContainer
{
    double offset = 50;

    public PathAdaptive buildPath()
    {
        double velocity = TankMaths.ticksToInchesPerSecond(RobotConfig.getInstance().getTankConfig().VELOCITY_MAX);
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(offset, offset, 0, 0));
        sWaypoints.add(new Waypoint(17 * 12.0 + offset, offset, 0, velocity));
        sWaypoints.add(new Waypoint(17 * 12.0 + offset, offset, 0, velocity));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    public RigidTransform2d getStartPose()
    {
        return new RigidTransform2d(new Translation2d(offset, offset), Rotation2d.fromDegrees(0.0));
    }

    public boolean isReversed()
    {
        return false;
    }
}
