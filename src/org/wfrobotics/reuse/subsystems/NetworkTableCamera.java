package org.wfrobotics.reuse.subsystems;

import java.util.ArrayList;
import java.util.Comparator;

import org.wfrobotics.TargetTable;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    
    public final int DESIRED_TARGETS;  // Reflective things to sense
    private final int SOURCE_DEFAULT = 0;
    private final int source;
    
    protected final NetworkTable sourceTable;
    protected final TargetTable table;
    
    protected ArrayList<TargetData> data;
    public int TargetCount = 0;
    public double DistanceFromCenter = 0;
    public double FullWidth = 0;
    public boolean InView = false;
    
    public NetworkTableCamera(String tableKey, int source, int desiredTargets)
    {
        this.source = source;
        sourceTable = NetworkTable.getTable("GRIP");
        table = new TargetTable(tableKey);
        data = new ArrayList<NetworkTableCamera.TargetData>();
        DESIRED_TARGETS = desiredTargets;
    }

    protected abstract void initDefaultCommand();   
    
    public abstract void run();
    
    protected void getUpdatedData()
    {
        table.update();
        data.clear();
        
        if(table.targetsFound > 0 && isEnabled())
        {
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
        TargetCount = data.size(); 
    }
    
    public void enable()
    {
        sourceTable.putNumber("CameraSource", source);

        DistanceFromCenter = 0;
        FullWidth = 0;
        InView = false;
    }
    
    public boolean isEnabled()
    {
        return table.cameraSource == source;
    }
    
    public void disable()
    {
        sourceTable.putNumber("CameraSource", SOURCE_DEFAULT);
        data.clear();

        DistanceFromCenter = 0;
        FullWidth = 0;
        InView = false;
    } 
}
