package org.wfrobotics.subsystems;

import java.util.ArrayList;
import java.util.Comparator;

import org.wfrobotics.vision.TargetTable;

import edu.wpi.first.wpilibj.command.Subsystem;
public abstract class Camera extends Subsystem {

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
    
    protected TargetTable table; 
    ArrayList<TargetData> data;

    
    public Camera(String tableKey)
    {
        table = new TargetTable(tableKey);
        data = new ArrayList<Camera.TargetData>();
    }

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }
    
    protected void getUpdatedData()
    {
        table.update();
        
        if(table.targetsFound > 0)
            data.clear();
        
        for(int i = 0; i < table.targetsFound; i++)
        {
            data.add(new TargetData(
                    table.x[i],
                    table.y[i],
                    table.width[i],
                    table.height[i]));
        }
    }
//    public TargetData getData()
//    {
//        
//        data.Yaw = table.getNumber("yaw", 0);
//        data.Pitch = table.getNumber("pitch", 0);
//        data.Depth = table.getNumber("depth", 0);
//        data.InView = table.getBoolean("yaw", false);
//        return data;
//        
//    }
}
