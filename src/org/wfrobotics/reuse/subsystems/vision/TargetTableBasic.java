package org.wfrobotics.reuse.subsystems.vision;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Grabs the latest raw data from the Network Table connection
 */
public class TargetTableBasic implements TargetTable
{
    NetworkTable table;
    
    public String name;
    
    public double[] x = {0};
    public double[] y = {0};
    public double[] height = {0};
    public double[] width = {0};
    public double[] solidity = {0};
    public double[] area = {0};
    
    public double cameraSource = 0;
    public double fps = 0;
    public double timestamp = 0;
    
    public int targetsFound = 0;
    
    public TargetTableBasic(String name)
    {
        table = NetworkTable.getTable("GRIP/" + name);
    }
    
    public void update()
    {
        x = table.getNumberArray("centerX", x);
        y = table.getNumberArray("centerY", y);
        height  = table.getNumberArray("height", height);
        width = table.getNumberArray("width", width);
        solidity = table.getNumberArray("solidity", solidity);
        area = table.getNumberArray("area", area);
        
        cameraSource = table.getNumber("CameraSource", cameraSource);
        fps = table.getNumber("fps", fps);
        timestamp = table.getNumber("timestamp", timestamp);
        
        targetsFound = Math.min(x.length, Math.min(y.length, Math.min(height.length, width.length)));
        SmartDashboard.putNumber("TargetsFound", targetsFound);
    }
    
    public Target getTarget(int i)
    {
        Target t = null;
        
        if(targetsFound > 0 && targetsFound <= i)
        {
            t = new Target(x[i], y[i], width[i], height[i]);
        }
        
        return t;
    }
    
    public int numTargets()
    {
        return targetsFound;
    }
    
    public double getcameraID()
    {
        return cameraSource;
    }
}
