package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.commands.AimerStop;
import com.taurus.hardware.MagnetoPotSRX;
import com.taurus.hardware.MagnetoPotSRX;
import com.taurus.robot.RobotMap;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AimerSubsystem extends Subsystem
{
    private final double ANGLE_MAX = 136 - 5;  // TODO - Determine upper safe limit through testing
    private final double ANGLE_MIN = -49 + 5;  // TODO - Determine lower safe limit through testing
    private final double TOLERANCE = 5;  // Degrees from desired angle that counts as that angle
    
    private Vision vision;
    private MagnetoPotSRX angle;
    private CANTalon motor;
    private PIDController pid;

    public AimerSubsystem()
    {
        motor = new CANTalon(RobotMap.CAN_SHOOTER_TALON_AIMER);
        motor.enableBrakeMode(true);
        
        pid = new PIDController(.2, 0, 0, 1);  //TODO update these values 
        angle = new MagnetoPotSRX(motor,360);
        angle.setAverage(true,6);
        vision = Vision.getInstance();
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new AimerStop());
    }
    
    /**
     * aims the shooter
     * @param changeInAngle 0 to 360
     * @return true if desired angle reached
     */
    public boolean aim(double changeInAngle)
    {
        double motorOutput;
        boolean done = false;
        
        // Update pot offsets if we change them in smart dashboard
        updatePotOffsets();
        
        double angleCurrent = angle.get();
        
        if (angleCurrent + changeInAngle > ANGLE_MAX || angleCurrent - changeInAngle < ANGLE_MIN)
        {
            // Being commanded to an unsafe angle
            motor.set(0);
        }
        else if (Math.abs(changeInAngle) < TOLERANCE)
        {
            // At the desired angle
            motor.set(0);
            done = true;
        }
        else
        {
            motorOutput = pid.update(changeInAngle);
            motor.set(motorOutput);
        }
        
        return done;
    }
    
    /**
     * aims the shooter at the detected target 
     * @return true when aimer is at desired angle
     */
    public boolean aim()
    {
        return aim(vision.getTarget().Pitch());
    }
    
    /***
     * basic speed function 
     * @param speed
     */
    public void setSpeed(double speed)
    {   
        updatePotOffsets();
        motor.set(speed);
    }
    
    public double getCurrentAngle()
    {
        return angle.get();
    }
    
    /**
     * Grab the latest pot offsets from Dashboard
     * TODO: add limit switch checks
     */
    public void updatePotOffsets()
    {
        SmartDashboard.putNumber("Aimer Angle", angle.get());
        SmartDashboard.putNumber("Aimer Raw", motor.getAnalogInRaw()/1023);

        angle.setOffset(Preferences.getInstance().getDouble("AimerPotOffset", 0));
        angle.setFullRange(Preferences.getInstance().getDouble("AimerPotScale", 360));
    }
}
