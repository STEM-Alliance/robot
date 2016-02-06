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
        return (getHeightL() + getHeightR()) / 2;
    }
    
    /**
     * set lift height
     * @param height we want to be at in millimeters
     * @return whether at desired height or not: will be a true/false and move accordingly
     */
    public boolean setHeight(double height) {
       
        double[] motorOutput;
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
        motorOutput = getPIDSpeed(height);
        
        arrived = Math.abs(getHeight()-height) <= LIMIT_TOLERANCE;        
        if(arrived)
        {
            stopLift();
        }
        else
        {
            setSpeed(motorOutput[0], motorOutput[1]);
        }
        
        return arrived;
    }
    
    /**
     * helper function: sets the motor speeds for the lift to the same value
     * @param right speed between -1 to 1
     * @param right speed between -1 to 1
     */
    private void setSpeed(double right, double left) {
        motorRight.set(right);
        motorLeft.set(left);        
    }
    
    private void stopLift() {
        setSpeed(0, 0);
    }
    
    /**
     * Calculate the desirable speeds to keep the two lift motors in sync.
     * This is advantageous because it corrects position drift due to one motor being faster than the other.
     * Author: David Lindner
     * @param height desired, millimeters
     * @return speed calculated (right, left)
     */
    private double[] getPIDSpeed(double height)
    {
        // Array convention -> (right side, left side)
        double[] heightActual = {getHeightR(), getHeightL()};
        double heightActualAverage = (heightActual[0] + heightActual[1]) / 2;
        double speedPidAverage = heightPID.update(height, heightActualAverage);
        double[] heightError = {heightActual[0] - heightActualAverage, heightActual[1] - heightActualAverage};  // Ex: 2 - 1.5 = .5 error
        double direction = Math.signum(speedPidAverage);  // Up: 1, Down: -1
        
        // If we are doing down, have error percentage be based on how far we are ahead/behind in that desired direction.
        // Ex: 10% ahead -> 10% behind, .1 error * -1 / 1 average + 1 = .91 rather than 1.1
        double[] heightErrorPercent = {(direction * heightError[0] / heightActualAverage + 1), 
                                       (direction * heightError[1] / heightActualAverage + 1)};  // Ex: 2 vs 1.5 -> 1.33 
        double[] speedCompensated = {speedPidAverage / heightErrorPercent[0], speedPidAverage / heightErrorPercent[1]};  // Ex: 1 / 1.33 = .75 speed
        
        // At this point, speed may be greater than one because we corrected for error.
        // Ex: Told one motor to go 110% of speed desired, the other to go 91% of speed desired. But speed desired is 1.
        // Scale it back to within range -1 to 1.        
        double[] speedCompensatedAbs = {Math.abs(speedCompensated[0]), Math.abs(speedCompensated[1])};
        
        for (int index = 0; index < 2; index++)
        {
            if (speedCompensatedAbs[index] > 1)
            {                
                // This is the side with positive error, and after compensation, happens to be above max magnitude of 1.
                // Scale back both speeds proportionally to how far the faster side is above max magnitude of 1.
                double percentAbove = speedCompensatedAbs[index] - 1;

                for (int side = 0; side < 2; side++)
                {
                    speedCompensated[side] /= percentAbove;
                }
                break;
            }
        }
        
        return speedCompensated;
    }
    
    private double getHeightR() {
        return (Math.sin(potRight.get())*711)*2;
    }
    
    private double getHeightL() {
        return (Math.sin(potLeft.get())*711)*2;
    }
}