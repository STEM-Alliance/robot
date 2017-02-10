package org.wfrobotics.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Provides information used to score a gear.
 * This subsystem translates pictures into data that commands can use to correct how the robot is aligned to the spring.
 *
 */
public class Aligning extends Subsystem 
{
    public class AlignData
    {
        public double Yaw = -20;
        public boolean InView = true;
    }
    
    AlignData data;
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO set a commmand IF this remains a subsystem
    }
    
    public AlignData getData()
    {
        AlignData data = new AlignData();
        
        // TODO get the data from the pi
        
        return data;
    }
    
    // TODO remove this after we can get info from the pi
    public void testIncrementData(double yawOffset, double pitchOffset)
    {
        data.Yaw += yawOffset;
    }
}
