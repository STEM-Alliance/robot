package org.wfrobotics.subsystems;


/**
 * Provides information used to score a gear.
 * This subsystem translates pictures into data that commands can use to correct how the robot is aligned to the spring.
 *
 */
public class Aligning extends Camera 
{    
    public Aligning()
    {
        super("GRIP/GearTarget");
    }
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO set a commmand IF this remains a subsystem
    }
}
