package org.wfrobotics.subsystems;

import org.wfrobotics.vision.Constants;

/**
 * Provides information used to shoot the ball.
 * This subsystem translates pictures into data that commands can use to correct how they are aiming.
 *
 */
public class CameraShooter extends Camera 
{   

    public CameraShooter()
    {
        super("Shooter");
    }
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO set a commmand IF this remains a subsystem
    }   
    
    public double DistanceToTarget()
    {
        //return (Constants.TargetHeightIn * Constants.FocalLengthIn) / data.Pitch;
        return 0;
    }
}