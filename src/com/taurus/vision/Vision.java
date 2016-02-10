package com.taurus.vision;

import java.util.ArrayList;
import java.util.Collections;

import com.ni.vision.NIVision;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision {

    NetworkTable table;

    ArrayList<Target> targets;

    public Vision()
    {
        table = NetworkTable.getTable("GRIP/TargetContours");
    }

    /**
     * Get the latest target(s) info from the NetworkTable
     * @return list of {@link Target}
     */
    public ArrayList<Target> UpdateTargets()
    {
        double[] defaultVals = {0};

        // get the latest values from the network table
        double[] x = table.getNumberArray("centerX", defaultVals);
        double[] y = table.getNumberArray("centerY", defaultVals);
        double[] area = table.getNumberArray("area", defaultVals);
        double[] h = table.getNumberArray("height", defaultVals);
        double[] w = table.getNumberArray("width", defaultVals);

        // reset the targets list
        targets = new ArrayList<Target>();

        // only add a target if it is a real target (area is greater than XX)
        if(area[0] > 0.1)
        {
            // add all found targets
            for (int i = 0; i < area.length; i++)
            {
                targets.add(new Target(x[i], y[i], area[i], h[i], w[i]));
            }
        }

        return targets;
    }

    /**
     * Get the last updated target values (does not update from NetworkTable)
     * @return list of {@link Target}
     */
    public ArrayList<Target> GetTargets()
    {
        return targets;
    }

    /**
     * Find the target with the largest area
     * @return {@link Target} with the largest area
     */
    public Target GetLargestTarget()
    {
        return Collections.max(targets, Target.AreaCompare);
    }

}
