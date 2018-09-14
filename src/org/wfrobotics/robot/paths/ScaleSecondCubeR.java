package org.wfrobotics.robot.paths;

import java.util.ArrayList;

import org.wfrobotics.reuse.config.PathBuilder;
import org.wfrobotics.reuse.config.PathBuilder.Waypoint;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.reuse.math.geometry.Pose2d;
import org.wfrobotics.reuse.math.geometry.Rotation2d;
import org.wfrobotics.reuse.math.geometry.Translation2d;
import org.wfrobotics.reuse.subsystems.control.Path;

public class ScaleSecondCubeR implements PathContainer {

    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(300,85,0,0));
        sWaypoints.add(new Waypoint(235,100,0,90));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    @Override
    public Pose2d getStartPose() {
        return new Pose2d(new Translation2d(300, 85), Rotation2d.fromDegrees(-150.0));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
    // WAYPOINT_DATA: [{"position":{"x":285,"y":65},"speed":0,"radius":0,"comment":""},{"position":{"x":245,"y":85},"speed":60,"radius":20,"comment":""},{"position":{"x":225,"y":89},"speed":60,"radius":0,"comment":""}]
    // IS_REVERSED: false
    // FILE_NAME: SameScaleSecondCubeRight
}