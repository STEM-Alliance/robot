package org.wfrobotics.robot.paths;

import java.util.ArrayList;

import org.wfrobotics.reuse.config.PathBuilder;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.reuse.config.PathBuilder.Waypoint;
import org.wfrobotics.reuse.math.rigidtransform.RigidTransform2d;
import org.wfrobotics.reuse.math.rigidtransform.Rotation2d;
import org.wfrobotics.reuse.math.rigidtransform.Translation2d;
import org.wfrobotics.reuse.subsystems.control.PathAdaptive;

public class SortOfDriveDistance implements PathContainer
{
    public PathAdaptive buildPath()
    {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0, 0, 0, 0));
        sWaypoints.add(new Waypoint(0, 17 * 12, 0, 40));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    public RigidTransform2d getStartPose()
    {
        return new RigidTransform2d(new Translation2d(0, 0), Rotation2d.fromDegrees(0.0));
    }

    public boolean isReversed()
    {
        return false;
    }
}
