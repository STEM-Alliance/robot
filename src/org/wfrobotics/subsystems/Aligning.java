package org.wfrobotics.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Provides information used to score a gear.
 * This subsystem translates pictures into data that commands can use to correct how the robot is aligned to the spring.
 * @author drlindne
 *
 */
public class Aligning extends Subsystem 
{
    public class AlignData
    {
        public double Yaw = 0;
        public boolean InView = false;
    }
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }
    
    public AlignData getData()
    {
        AlignData data = new AlignData();
        
        // TODO DRL Take a frame from the Camera object, process it, return information so the command can make decisions
        
        return data;
    }
}
