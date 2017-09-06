package org.wfrobotics.reuse.subsystems.vision;

public interface TargetTable 
{
    public void update();
    public Target getTarget(int i);
    public int numTargets();
    public double getcameraID();
}
