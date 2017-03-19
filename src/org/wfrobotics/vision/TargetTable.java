package org.wfrobotics.vision;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TargetTable {

    NetworkTable table;
    
    public String name;

    //TODO figure out how to get this automatically
    public double imageWidth = 640.0;
    public double imageHeight = 480.0;
    
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
    
    public TargetTable(String name)
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

}
