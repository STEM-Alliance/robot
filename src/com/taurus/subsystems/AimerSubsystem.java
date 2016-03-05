package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.commands.AimerStop;
import com.taurus.hardware.MagnetoPotSRX;
import com.taurus.robot.RobotMap;
import com.taurus.vision.Target;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AimerSubsystem extends Subsystem
{
    private final double MOTOR_TOLERANCE = 0.05;
    private static final double ANGLE_MAX = 140 - 5;
    private static final double ANGLE_MIN = -50 + 5;
    private final double TOLERANCE = .75;  // Degrees from desired angle that counts as that angle

    public final double ANGLE_GRAB_FROM_BOTTOM_FRONT = 82;
    
    private Vision vision;
    private MagnetoPotSRX angle;
    private CANTalon motor;
    private PIDController pid;    
    
    private Relay leds;

    public AimerSubsystem()
    {
        motor = new CANTalon(RobotMap.CAN_SHOOTER_TALON_AIMER);
        motor.enableBrakeMode(true);
        motor.setInverted(true);
        
        pid = new PIDController(.1, 0, 0, .25);  //TODO update these values 
        angle = new MagnetoPotSRX(motor,360);
        angle.setAverage(true,6);
        vision = Vision.getInstance();
        
        leds = new Relay(RobotMap.PIN_RELAY_LEDS, Direction.kForward);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new AimerStop());
    }
    
    /**
     * aims the shooter
     * @param changeInAngle -360 to 360
     * @return true if desired angle reached
     */
    public boolean aim(double changeInAngle)
    {
        double motorOutput;
        boolean done = false;
        
        // Update pot offsets if we change them in smart dashboard
        updatePotOffsets();
        
        double angleCurrent = getCurrentAngle();
        double desiredAngle = angleCurrent + changeInAngle;
        

        updatedPIDConstants();
        motorOutput = pid.update(changeInAngle);
       
        if (desiredAngle > ANGLE_MAX || desiredAngle < ANGLE_MIN)
        {
            // Being commanded to an unsafe angle
            SmartDashboard.putString("AimerAim", "Unsafe");
            setSpeed(0);
        }
        else if (Math.abs(changeInAngle) < TOLERANCE)
        {
            // At the desired angle
            SmartDashboard.putString("AimerAim", "At Angle");
            setSpeed(0);
            done = true;
        }
        else
        {
            SmartDashboard.putString("AimerAim", "Moving " + motorOutput);
            setSpeed(motorOutput);
        }
        
        return done;
    }

    /**
     * aims the shooter
     * @param desiredAngle -360 to 360
     * @return true if desired angle reached
     */
    public boolean aimTo(double desiredAngle)
    {
        return aim(desiredAngle - getCurrentAngle());
    }

    /**
     * aims the shooter between two angles
     * @param minAngle -360 to 360
     * @param maxAngle -360 to 360
     * @return true if desired angle reached
     */
    public boolean aimBetween(double minAngle, double maxAngle)
    {
        double curr = getCurrentAngle();
        
        if(maxAngle < minAngle)
        {
            double temp = maxAngle;
            maxAngle = minAngle;
            minAngle = temp;
        }
        
        if(maxAngle > ANGLE_MAX)
        {
            maxAngle = ANGLE_MAX;
        }
        if(minAngle < ANGLE_MIN)
        {
            minAngle = ANGLE_MIN;
        }
        
        if(curr > maxAngle)
        {
            return aim(maxAngle - curr);
        }
        else if (curr < minAngle)
        {
            return aim(minAngle - curr);
        }
        else
        {
            return true;
        }
    }
    
    private void updatedPIDConstants()
    {

        pid.setP(Preferences.getInstance().getDouble("AimerPID_P", .3));
        pid.setI(Preferences.getInstance().getDouble("AimerPID_I", 0));
        pid.setD(Preferences.getInstance().getDouble("AimerPID_D", 0));
    }
    
    /**
     * aims the shooter at the detected target 
     * @return true when aimer is at desired angle
     */
    public boolean aim(Target target)
    {
        boolean done = false;
        
        if(target != null)
        {
            done = aim(vision.getTarget().Pitch());
        }
//        else
//        {
//            done = aim(0);
//        }
        return done;
    }
    
    /***
     * basic speed function 
     * @param speed
     */
    public void setSpeed(double speed)
    {   
        updatePotOffsets();
        
        // protect our limit ranges
        double curAngle = getCurrentAngle();
        boolean direction = Math.signum(speed) > 0 ? true : false;
        boolean valid = true;
        
        if(curAngle > ANGLE_MAX)
        {
            if(!direction)
                valid = true;
            else
                valid = false;
        }
        else if(curAngle < ANGLE_MIN)
        {
            if(direction)
                valid = true;
            else
                valid = false;   
        }
        
        if(valid)
        {
            motor.set(speed);
        }
        else
        {
            motor.set(0);
        }
    
    }
    
    public double getCurrentAngle()
    {
        return Utilities.wrapToRange(angle.get(),-180,180);
    }
    
    /**
     * Grab the latest pot offsets from Dashboard
     * TODO: add limit switch checks
     */
    public void updatePotOffsets()
    {
        SmartDashboard.putNumber("Aimer Angle", angle.get());
        //SmartDashboard.putNumber("Aimer Raw", motor.getAnalogInRaw()/1023.0);

        angle.setOffset(Preferences.getInstance().getDouble("AimerPotOffset", 0));
    }
    
    public void enableLEDs(boolean enable)
    {
        if(enable)
        {
            leds.set(Value.kForward);
        }
        else
        {
            leds.set(Value.kOff);
        }
    }
}
