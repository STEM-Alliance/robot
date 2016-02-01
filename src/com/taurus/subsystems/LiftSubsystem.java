package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.commands.LiftStop;
import com.taurus.hardware.MagnetoPot;
import com.taurus.hardware.MagnetoPotSRX;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class LiftSubsystem extends Subsystem{
    public final double LIMIT_UPPER = 1200;  // millimeters
    public final double LIMIT_LOWER = 325;  //millimeters
    public final double LIMIT_TOLERANCE = 20;  // millimeters
    
    private CANTalon motorLeft;
    private CANTalon motorRight;
    private MagnetoPot potLeft;
    private MagnetoPot potRight;  // TODO - DRL remove if design changes and is unused
    private PIDController heightPID;
        
    /**
     * Constructor
     */
    public LiftSubsystem() {
        motorLeft = new CANTalon(RobotMap.PIN_LIFT_TALON_L);
        motorRight = new CANTalon(RobotMap.PIN_LIFT_TALON_R);
        
        potLeft = new MagnetoPot(2, 360);
        potRight = new MagnetoPot(3, 360);
        
        heightPID = new PIDController(1, 0, 0, 1);
    }
    
    protected void initDefaultCommand()
    {
        setDefaultCommand(new LiftStop());
    }
    
    public double getHeight() {
        return (Math.sin(potLeft.get())*711)*2;
    }
    
    /**
     * set lift height
     * @param height we want to be at in millimeters
     * @return whether at desired height or not: will be a true/false and move accordingly
     */
    public boolean setHeight(double height) {
       
        double motorOutput = heightPID.update(height, getHeight());
        boolean arrived;
        
        // Error check height, set to within bounds if needed
        if(height >= LIMIT_UPPER)
        {
            height = LIMIT_UPPER;
        } 
        else if(height <= LIMIT_LOWER)
        {
            height = LIMIT_LOWER;
        }
        
        arrived = Math.abs(getHeight()-height) <= LIMIT_TOLERANCE;
        
        if(arrived)
        {
            stopLift();
        }
        else
        {
            setSpeed(motorOutput);
        }
        
        return arrived;
    }
    
    /**
     * helper function: sets the motor speeds for the lift to the same value
     * @param speed between 0 to 1
     */
    private void setSpeed(double speed) {
        motorRight.set(speed);
        motorLeft.set(speed);        
    }
    
    private void stopLift() {
        setSpeed(0);
    }
}
