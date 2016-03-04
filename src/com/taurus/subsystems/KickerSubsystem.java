package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.commands.KickerStop;
import com.taurus.hardware.MagnetoPot;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class KickerSubsystem extends Subsystem
{
    private static final double ANGLE_MAX = 180;
    private static final double ANGLE_MIN = 0;
    private final double TOLERANCE = 10;  // Degrees from desired angle that counts as that angle

    private MagnetoPot angle;
    private CANTalon motor;
    private PIDController pid;

    public KickerSubsystem()
    {
        motor = new CANTalon(RobotMap.CAN_KICKER_TALON);
        motor.enableBrakeMode(true);
        motor.setInverted(false);
        
        pid = new PIDController(.1, 0, 0, .5);  //TODO update these values 
        angle = new MagnetoPot(RobotMap.PIN_ANG_KICKER,360);
        angle.setAverage(true , 2);        
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new KickerStop());
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
       
        if (desiredAngle > ANGLE_MAX  ||
            desiredAngle < ANGLE_MIN)
        {
            // Being commanded to an unsafe angle
            SmartDashboard.putString("KickerAngle", "Unsafe");
            setSpeed(0);
        }
        else if (Math.abs(changeInAngle) < TOLERANCE)
        {
            // At the desired angle
            SmartDashboard.putString("KickerAngle", "At Angle");
            setSpeed(0);
            done = true;
        }
        else
        {
            SmartDashboard.putString("KickerAngle", "Moving " + motorOutput);
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

    private void updatedPIDConstants()
    {

        pid.setP(Preferences.getInstance().getDouble("KickerPID_P", .3));
        pid.setI(Preferences.getInstance().getDouble("KickerPID_I", 0));
        pid.setD(Preferences.getInstance().getDouble("KickerPID_D", 0));
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
            SmartDashboard.putNumber("KickerSpeed", speed);
        }
        else
        {
            motor.set(0);
            SmartDashboard.putNumber("KickerSpeed", 0);
        }
    
    }
    
    public double getCurrentAngle()
    {
        return Utilities.wrapToRange(angle.get(), -90, 270);
    }
    
    /**
     * Grab the latest pot offsets from Dashboard
     * TODO: add limit switch checks
     */
    public void updatePotOffsets()
    {
        SmartDashboard.putNumber("Kicker Angle", angle.get());
        SmartDashboard.putNumber("Kicker Raw", motor.getAnalogInRaw()/1023.0);

        angle.setOffset(Preferences.getInstance().getDouble("KickerPotOffset", 0));
        angle.setFullRange(Preferences.getInstance().getDouble("KickerPotScale", 360));
    }
}
