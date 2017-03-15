package org.wfrobotics.vision;

import java.util.ArrayList;
import java.util.Comparator;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public abstract class NetworkTableCamera extends Subsystem 
{
    public class TargetData 
    {
        public TargetData(double x, double y, double w, double h)
        {
            this.x = x; this.y = y; this.width = w; this.height = h;
        }
        
        public double x = 0;
        public double y = 0;
        public double width = 0;
        public double height = 0;        
    }

    public static final Comparator<TargetData> AreaCompare = new Comparator<TargetData>()
    {
        @Override
        public int compare(TargetData o1, TargetData o2)
        {
            double a1 = o1.width*o1.height;
            double a2 = o2.width*o2.height;
            
            if (a1  > a2)
            {
                return 1;
            }
            else if (a1 == a2)
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
    };
    
    protected NetworkTable sourceTable;
    protected TargetTable table; 
    protected ArrayList<TargetData> data;

    public NetworkTableCamera(String tableKey)
    {
        sourceTable = NetworkTable.getTable("GRIP");
        table = new TargetTable(tableKey);
        data = new ArrayList<NetworkTableCamera.TargetData>();
    }

    protected abstract void initDefaultCommand();
    
    protected void getUpdatedData()
    {
        table.update();
        
        if(table.targetsFound > 0)
        {
            data.clear();
            
            for(int i = 0; i < table.targetsFound; i++)
            {
                TargetData d = new TargetData(
                        table.x[i],
                        table.y[i],
                        table.width[i],
                        table.height[i]);
                data.add(d);
            }
        }
        else
        {
            data.clear();
        }
    }
    
    public void enable(int source)
    {
        sourceTable.putNumber("CameraSource", source);
    }
}
