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
        public double Yaw = 0;
        public double Pitch = 0;
        public double Depth = 0;
        public boolean InView = false;
    }
    
    // TODO DRL private Camera camera;
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }
    
    public TargetData getData()
    {
        TargetData data = new TargetData();
        
        // TODO DRL Take a set of data from the rasp pi, return information so the command can make decisions
        
        return data;
    }
}
