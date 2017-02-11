package org.wfrobotics.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public abstract class Camera extends Subsystem {

    public class TargetData 
    {
        public double Yaw = 20; // x 
        public double Pitch = 20; // y 
        public double Depth = 20;// z
        public boolean InView = true;
    }
    
    NetworkTable table; 
    TargetData data;

    
    public Camera(String tableKey)
    {
        table = NetworkTable.getTable(tableKey);
        data = new TargetData();
    }

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }
    
    public TargetData getData()
    {
        data.Yaw = table.getNumber("yaw", 0);
        data.Pitch = table.getNumber("pitch", 0);
        data.Depth = table.getNumber("depth", 0);
        data.InView = table.getBoolean("yaw", false);
        return data;
        
    }
    
    public void testIncrementData(double yawOffset, double pitchOffset)
    {
        data.Yaw += yawOffset;
        data.Pitch += pitchOffset;
    }
    

}
