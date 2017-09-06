package org.wfrobotics.reuse.subsystems.vision;

import java.util.ArrayList;
import java.util.Comparator;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Base class for getting vision updates from processor on network table 
 */
public abstract class NetworkTableCamera extends Subsystem 
{
    public static final Comparator<Target> AreaCompare = new Comparator<Target>()
    {
        @Override
        public int compare(Target o1, Target o2)
        {
            double a1 = o1.width*o1.height;
            double a2 = o2.width*o2.height;
            
            if (a1  > a2) { return 1; }
            else if (a1 == a2) { return 0; }
            else { return -1; }
        }
    };
    
    private final int source;
    private boolean enabled;
    
    protected final NetworkTable sourceTable;
    protected TargetTable table;
    protected ArrayList<Target> data;

    public NetworkTableCamera(int source)
    {
        this.source = source;
        sourceTable = NetworkTable.getTable("GRIP");
        data = new ArrayList<Target>();
        enabled = false;
    }

    protected abstract void initDefaultCommand();
    public abstract void processData();
    public abstract double getDistanceFromCenter();
    public abstract double getFullWidth();
    public abstract boolean getInView();
    protected abstract void notifyEnabled();
    
    public void update()
    {
        if (enabled)
        {
            int numTargets;
            
            table.update();
            numTargets = table.numTargets();
            data.clear();

            if(table.numTargets() > 0)
            {
                for(int i = 0; i < numTargets; i++)
                {
                    data.add(table.getTarget(i));
                }
            }
            processData();
        }
    }
    
    public void enable()
    {
        notifyEnabled();
        sourceTable.putNumber("CameraSource", source);
        enabled = true;
    }
    
    public void disable()
    {
        enabled = false;
        sourceTable.putNumber("CameraSource", -1);
        notifyEnabled();
    } 
    
    public boolean isEnabled()
    {
        return enabled && table.getcameraID() == source;
    }
}
