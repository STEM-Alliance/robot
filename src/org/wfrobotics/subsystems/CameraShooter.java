package org.wfrobotics.subsystems;

import org.wfrobotics.commands.ShooterDetection;
import org.wfrobotics.vision.NetworkTableCamera;

/**
 * Provides information used to shoot the ball.
 * This subsystem translates pictures into data that commands can use to correct how they are aiming.
 *
 */
public class CameraShooter extends NetworkTableCamera 
{   

    public CameraShooter()
    {
        super("Shooter");
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new ShooterDetection(ShooterDetection.MODE.OFF));
    }
    
    public void run()
    {
        getUpdatedData();
    }
    
    public double DistanceToTarget()
    {
        //return (Constants.TargetHeightIn * Constants.FocalLengthIn) / data.Pitch;
        return 0;
    }
}