package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.hardware.MagnetoPot;
import com.taurus.robot.RobotMap;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class AimerSubsystem extends Subsystem
{
    private Vision vision;
    private MagnetoPot aimAngle;
    private CANTalon aimer;
    private PIDController aimerPID;

    public AimerSubsystem()
    {
        aimer = new CANTalon(RobotMap.PIN_SHOOTER_TALON_AIMER);
        aimerPID = new PIDController(1, 0, 0, 1);//TODO update these values 
        aimAngle = new MagnetoPot(0,360);
        vision = Vision.getInstance();
    }

    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub        
    }
    
    /**
     * aims the shooter
     * @param changeInAngle 0 to 360
     * @return true if angle reached, false if not
     */
    public boolean aim(double changeInAngle)
    {
        double motorOutput = aimerPID.update(changeInAngle);//TODO add limits for angle

        if(Math.abs(changeInAngle) <= 5){
            aimer.set(0);
            return true;
        } else {
            aimer.set(motorOutput);
            return false;
        }
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
}
