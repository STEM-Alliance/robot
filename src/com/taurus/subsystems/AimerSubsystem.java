package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.commands.AimerStop;
import com.taurus.hardware.MagnetoPotSRX;
import com.taurus.robot.RobotMap;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;

public class AimerSubsystem extends Subsystem
{
    private final double ANGLE_MAX = 100;  // TODO - Determine upper safe limit through testing
    private final double ANGLE_MIN = 0;  // TODO - Determine lower safe limit through testing
    private final double DEADBAND = 5;
    
    private Vision vision;
    private MagnetoPotSRX aimAngle;
    private CANTalon aimer;
    private PIDController aimerPID;

    public AimerSubsystem()
    {
        aimer = new CANTalon(RobotMap.PIN_SHOOTER_TALON_AIMER);
        aimerPID = new PIDController(1, 0, 0, 1);  //TODO update these values 
        aimAngle = new MagnetoPotSRX(aimer,360);
        vision = Vision.getInstance();
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new AimerStop());
    }
    
    /**
     * aims the shooter
     * @param changeInAngle 0 to 360
     * @return true if angle reached, false if not
     */
    public boolean aim(double changeInAngle)
    {
        double motorOutput;
        boolean done = false;
        
        // Update pot offsets if we change them in smart dashboard
        updatePotOffsets();
        
        // Check that we are being commanded to a safe angle
        if (aimAngle.get() + changeInAngle < ANGLE_MAX || aimAngle.get() - changeInAngle > ANGLE_MIN)
            // TODO - DRL make sure this code works regardless of if the sensor is returning positive or
            //        negative values however it is currently hooked up
        {
            // Get the PID speed
            motorOutput = aimerPID.update(changeInAngle);  //TODO add limits for angle

            // Determine if we are at the desired angle, set the motor speed accordingly
            if (Math.abs(changeInAngle) < DEADBAND)
            {
                aimer.set(0);
                done = true;
            }
            else
            {
                aimer.set(motorOutput);
            }
        }
        
        return done;
    }
    /***
     * basic speed function 
     * @param speed
     */
    public void setSpeed(double speed){
        aimer.set(speed);
    }
    
    /**
     * aims the shooter at the detected target 
     * @return true when aimer is at desired angle
     */
    public boolean aim()
    {
        return aim(vision.getTarget().Pitch());
    }
    
    public double getCurrentAngle()
    {
        return aimAngle.get();
    }
    
    /**
     * Grab the latest pot offsets from Dashboard
     * TODO: add limit switch checks
     */
    private void updatePotOffsets()
    {
        aimAngle.setOffset(Preferences.getInstance().getDouble("AimerPotOffset", 0));
        aimAngle.setFullRange(Preferences.getInstance().getDouble("AimerPotScale", 0));
    }
}
