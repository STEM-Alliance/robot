package org.wfrobotics.robot.paths;

import java.util.ArrayList;

import org.wfrobotics.reuse.config.PathBuilder;
import org.wfrobotics.reuse.config.PathBuilder.Waypoint;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.reuse.math.geometry.Pose2d;
import org.wfrobotics.reuse.math.geometry.Rotation2d;
import org.wfrobotics.reuse.math.geometry.Translation2d;
import org.wfrobotics.reuse.subsystems.control.Path;

public class StartToOppositeScaleR implements PathContainer {

    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0,50,0,0));
        sWaypoints.add(new Waypoint(140,40,50,120));
        sWaypoints.add(new Waypoint(235,60,30,120));
        sWaypoints.add(new Waypoint(237,150,10,120));
        sWaypoints.add(new Waypoint(240,220,20,70));
        sWaypoints.add(new Waypoint(265,243,15,50));
        sWaypoints.add(new Waypoint(298,245,0,40));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }

    @Override
    public Pose2d getStartPose() {
        return new Pose2d(new Translation2d(0, 50), Rotation2d.fromDegrees(0.0));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
    // WAYPOINT_DATA: [{"position":{"x":0,"y":50},"speed":0,"radius":0,"comment":""},{"position":{"x":220,"y":50},"speed":60,"radius":30,"comment":""},{"position":{"x":240,"y":100},"speed":60,"radius":20,"comment":""},{"position":{"x":240,"y":245},"speed":60,"radius":10,"comment":""},{"position":{"x":280,"y":245},"speed":60,"radius":0,"comment":""}]
    // IS_REVERSED: false
    // FILE_NAME: OppositeScaleRight
}