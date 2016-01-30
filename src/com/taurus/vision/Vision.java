package com.taurus.vision;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Vision {

    private final static String[] GRIP_ARGS = new String[] {
            "/usr/local/frc/JRE/bin/java",
            "-jar",
            "/home/lvuser/grip.jar",
            "/home/lvuser/project.grip" };

    private final NetworkTable table;

    ArrayList<Target> targets;

    public Vision()
    {
        table = NetworkTable.getTable("GRIP/TargetContours");
        RestartGRIP();
    }
    
    /**
     * Stop and restart GRIP
     * @return true if started
     */
    public boolean RestartGRIP()
    {
        StopGRIP();
        return StartGRIP();
    }
    
    /**
     * Start a new GRIP Process
     * @return true if started
     */
    public boolean StartGRIP()
    {
        /* Run GRIP in a new process */
        try {
            Runtime.getRuntime().exec(GRIP_ARGS);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kill any running GRIP processes
     */
    public void StopGRIP()
    {
        try {
            Process p1 = Runtime.getRuntime().exec(new String[] { "pkill", "-f \"java.*grip\""});
            OutputStream output = p1.getOutputStream();
            
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                //targets.add(new Target(x[i], y[i], area[i], h[i], w[i]));
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
