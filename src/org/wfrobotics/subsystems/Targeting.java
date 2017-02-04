package org.wfrobotics.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Provides information used to shoot the ball.
 * This subsystem translates pictures into data that commands can use to correct how they are aiming.
 * @author drlindne
 *
 */
public class Targeting extends Subsystem 
{
    
    
    public class TargetData
    {
        public double Yaw = 20; // x 
        public double Pitch = 20; // y 
        public double Depth = 20;// z
        public boolean InView = true;
        
    }
    
    TargetData data;
    // TODO DRL private Camera camera;
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }
    
    public TargetData getData()
    {
        data = new TargetData();
        
        // TODO DRL Take a set of data from the rasp pi, return information so the command can make decisions
        
        return data;
    }
    public void testIncrementData(double yawOffset, double pitchOffset)
    {
        data.Yaw += yawOffset;
        data.Pitch += pitchOffset;
    }
    
}
