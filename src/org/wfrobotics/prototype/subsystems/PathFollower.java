package org.wfrobotics.prototype.subsystems;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;

/** Streams path as motor commands (trajectory points) */
public class PathFollower
{
    private PathSource profile;
    private double[][][] points;
    private int last;
    private int current;

    public PathFollower()
    {
        profile = new FauxProfile(3000, 6000);  // TODO Dynamically pick (CSV, on the fly, etc)
        points = new double[0][0][0];
        last = 0;
        current = 0;
    }

    public int length()
    {
        return points.length;
    }

    public void reload()
    {
        points = profile.get(0, profile.length());  // TODO Stream it?
        last = points.length - 1;
        current = 0;
    }

    public TrajectoryPoint[][] next(int numPoints)
    {
        int length = (current + numPoints < length()) ? numPoints : length() - current;
        TrajectoryPoint[][] buffer = new TrajectoryPoint[length][2];

        for (int index = 0; index < length; index++)
        {
            for (int side = 0; side < 2; side++)
            {
                buffer[index][side] = new TrajectoryPoint();
                buffer[index][side].position = points[current][side][0];
                buffer[index][side].velocity = points[current][side][1];
                buffer[index][side].profileSlotSelect0 = 0;
                buffer[index][side].profileSlotSelect1 = 0;
                buffer[index][side].timeDur = TrajectoryDuration.Trajectory_Duration_10ms;
                buffer[index][side].zeroPos = current == 0;  // Will zero the sensor's position
                buffer[index][side].isLastPoint = current == last;  // Marks no underrun on timeout
            }
            current++;
        }

        return buffer;
    }
}
