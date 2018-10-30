package org.wfrobotics.robot;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.wfrobotics.reuse.math.geometry.Translation2d;

import edu.wpi.first.wpilibj.Timer;

/**
 * A class that is used to keep track of all goals detected by the vision system. As goals are detected/not detected
 * anymore by the vision system, function calls will be made to create, destroy, or update a goal track.
 *
 * This helps in the goal ranking process that determines which goal to fire into, and helps to smooth measurements of
 * the goal's location over time.
 *
 * @see GoalTracker.java
 */
public class GoalTrack {
    public static double kMaxGoalTrackAge = 1.0;
    public static double kMaxTrackerDistance = 18.0;
    public static double kCameraFrameRate = 30.0;

    private final boolean javaTimestamps;

    Map<Double, Translation2d> mObservedPositions = new TreeMap<>();
    Translation2d mSmoothedPosition = null;
    int mId;

    private GoalTrack() {
        javaTimestamps = System.getProperty("os.name").contains("Windows");
    }

    /**
     * Makes a new track based on the timestamp and the goal's coordinates (from vision)
     */
    public static GoalTrack makeNewTrack(double timestamp, Translation2d first_observation, int id) {
        GoalTrack rv = new GoalTrack();
        rv.mObservedPositions.put(timestamp, first_observation);
        rv.mSmoothedPosition = first_observation;
        rv.mId = id;
        return rv;
    }

    public void emptyUpdate() {
        pruneByTime();
    }

    /**
     * Attempts to update the track with a new observation.
     *
     * @return True if the track was updated
     */
    public boolean tryUpdate(double timestamp, Translation2d new_observation) {
        if (!isAlive()) {
            return false;
        }
        double distance = mSmoothedPosition.inverse().translateBy(new_observation).norm();
        if (distance < kMaxTrackerDistance) {
            mObservedPositions.put(timestamp, new_observation);
            pruneByTime();
            return true;
        }
        emptyUpdate();
        return false;
    }

    public boolean isAlive() {
        return mObservedPositions.size() > 0;
    }

    /**
     * Removes the track if it is older than the set "age" described in the Constants file.
     *
     * @see Constants.java
     */
    void pruneByTime() {
        double delete_before = getTimestamp() - kMaxGoalTrackAge;
        for (Iterator<Map.Entry<Double, Translation2d>> it = mObservedPositions.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Double, Translation2d> entry = it.next();
            if (entry.getKey() < delete_before) {
                it.remove();
            }
        }
        if (mObservedPositions.isEmpty()) {
            mSmoothedPosition = null;
        } else {
            smooth();
        }
    }

    /**
     * Averages out the observed positions based on an set of observed positions
     */
    void smooth() {
        if (isAlive()) {
            double x = 0;
            double y = 0;
            for (Map.Entry<Double, Translation2d> entry : mObservedPositions.entrySet()) {
                x += entry.getValue().x();
                y += entry.getValue().y();
            }
            x /= mObservedPositions.size();
            y /= mObservedPositions.size();
            mSmoothedPosition = new Translation2d(x, y);
        }
    }

    public Translation2d getSmoothedPosition() {
        return mSmoothedPosition;
    }

    public double getLatestTimestamp() {
        return mObservedPositions.keySet().stream().max(Double::compareTo).orElse(0.0);
    }

    public double getStability() {
        return Math.min(1.0, mObservedPositions.size() / (kCameraFrameRate * kMaxGoalTrackAge));
    }

    public int getId() {
        return mId;
    }

    private double getTimestamp()
    {
        if (javaTimestamps)
        {
            return System.currentTimeMillis();  // ms since epoch - java testing
        }
        return Timer.getFPGATimestamp();  // ms - RIO
    }
}
