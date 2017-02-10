package org.wfrobotics.subsystems;

import org.wfrobotics.vision.Constants;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Provides information used to shoot the ball.
 * This subsystem translates pictures into data that commands can use to correct how they are aiming.
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
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO set a commmand IF this remains a subsystem
    }
    
    public TargetData getData()
    {
        data = new TargetData();
        
        // TODO get the data from the pi
        
        return data;
    }
    
    // TODO remove this after we can get info from the pi
    public void testIncrementData(double yawOffset, double pitchOffset)
    {
        data.Yaw += yawOffset;
        data.Pitch += pitchOffset;
    }
    
    public double DistanceToTarget()
    {
        return (Constants.TargetHeightIn * Constants.FocalLengthIn) / data.Pitch;
    }
}