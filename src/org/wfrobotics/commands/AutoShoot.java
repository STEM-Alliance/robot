package org.wfrobotics.commands;

public class AutoShoot 
{
    public enum MODE {DIRECT, HOPPER};
    
    public AutoShoot()
    {
        // Just like AutoGear, think through this by writing pseudocode, then implement
        
        // TODO Shoot directly at the boiler or do a sequence of commands to load from the hopper and shoot
            // Many steps for the latter
    }
    
    protected void end()
    {
        // TODO definitely some cleanup here
    }
    
    protected void interrupted()
    {
        end();
    }
}
