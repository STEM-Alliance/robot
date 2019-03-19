package org.wfrobotics.robot.paths;

import java.util.ArrayList;

import org.wfrobotics.reuse.config.PathBuilder;
import org.wfrobotics.reuse.config.PathBuilder.Waypoint;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.reuse.math.geometry.Pose2d;
import org.wfrobotics.reuse.math.geometry.Rotation2d;
import org.wfrobotics.reuse.math.geometry.Translation2d;
import org.wfrobotics.reuse.subsystems.drive.Path;
import org.wfrobotics.reuse.subsystems.drive.TankMaths;

public class StartToScaleR implements PathContainer {

    @Override
    public Path buildPath() {
        double velocity = TankMaths.kVelocityMaxInchesPerSecond;
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(0,50,0,0));
        sWaypoints.add(new Waypoint(150,50,70,velocity));
        sWaypoints.add(new Waypoint(250,70,20,velocity));
        sWaypoints.add(new Waypoint(300,85,0,60));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);

        //        List<Pose2d> poses = Arrays.asList(new Pose2d[]{
        //            new Pose2d(0, 50, Rotation2d.fromDegrees(0)),
        //            new Pose2d(150, 50, Rotation2d.fromDegrees(0)),
        //            new Pose2d(300, 85, Rotation2d.fromDegrees(12)),
        //        });
        //
        //        return PathBuilder.buildPathFromPoses(poses, velocity, velocity/2);
    }

    @Override
    public Pose2d getStartPose() {
        return new Pose2d(new Translation2d(0, 50), Rotation2d.fromDegrees(0.0));
    }

    @Override
    public boolean isReversed() {
        return false;
    }
    // WAYPOINT_DATA: [{"position":{"x":0,"y":50},"speed":0,"radius":0,"comment":""},{"position":{"x":150,"y":50},"speed":60,"radius":40,"comment":""},{"position":{"x":285,"y":65},"speed":60,"radius":0,"comment":""}]
    // IS_REVERSED: false
    // FILE_NAME: SameScaleRight
}